#include <cstdio>
#include <cstdlib>
#include <ctime>
#include <cstring>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/epoll.h>
#include <thread>
#include <unordered_set>
#include <signal.h>
#include <errno.h>
#include <error.h>
#include <map>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <deque>

// global value for server file descriptor
int servFd;

// client sockets
std::unordered_set<int> clientFds;
// map containing pairs clientFd: <client-login>
std::map<int, std::string> players;
// map containing pairs clientFd: clientPoints
std::map<int, int> points;
// 
std::map<int, std::string> answers;
int maxRounds = 10;
int currentRound = 0;
bool gameStarted = false;
bool roundStarted = false;
int playersNum = 0;
int activePlayers = 0;
bool currentCategory = 0; // 0 - countries, 1 - cities
char currentLetter = 'A';
std::unordered_set<std::string> cities;
std::unordered_set<std::string> countries;

// message queue for clients, contains messages to be sent and clientfd using vectors
std::deque<std::pair<int, std::string>> msgQueue;



// handles client disconnection
void handleDisconnect(int clientFd){
    clientFds.erase(clientFd);
    players.erase(clientFd);
    points.erase(clientFd);
    answers.erase(clientFd);
    shutdown(clientFd, SHUT_RDWR);
    close(clientFd);
    playersNum--;
}

// converts char arr into an int
int getNumFromBuf(char buf[2]){
    int res = 10*((int) buf[0] - '0') + (buf[1]) - '0';
    return res;
}

// checks if login is in use, if not adds it to login map
bool handleLogin(std::string login, int clientFd){
    for (auto i = players.begin(); i != players.end(); i++){
        if (i->second == login){
            return false;
        }
    }
    players[clientFd] = login;
    return true;
}

// initializes cities and countries sets
void initData(){
    std::string line;
    std::ifstream file1("cities.txt");
    if (file1.is_open()) {
        while (getline(file1, line)) {
            cities.insert(line);
        }
        file1.close();
    }
    std::ifstream file2("countries.txt");
    if (file2.is_open()) {
        while (getline(file2, line)) {
            countries.insert(line);
        }
        file2.close();
    }
}

void assignPoints(){
    for (auto i = answers.begin(); i != answers.end(); i++){
        std::string ans = i->second;
        if (ans.rfind(currentLetter, 0) == 0 && ((!currentCategory && countries.count(ans)) || (currentCategory && cities.count(ans)))){
            points[i->first] = 1;
        }
    }
}

void startRound(){
    roundStarted = true;
    currentRound++;
    currentLetter = 'A' + rand()%26;
    currentCategory = rand()%2;
    char msg[256] {};
    sprintf(msg, "ROUND:%d:%c:%d", currentRound, currentLetter, currentCategory);
    for (auto i = clientFds.begin(); i != clientFds.end(); i++){
        msgQueue.push_back(std::make_pair(*i, std::string(msg)));
    }
    std::cout << msg << std::endl;
}

void startGame(){
    gameStarted = true;
    startRound();
}

int main(int argc, char ** argv) {
    if(argc!=2){
        printf("Usage: %s <port>\n", argv[0]);
        return 1;
    }
    char * endp;
    long port = strtol(argv[1], &endp, 10);
    if(*endp || port > 65535 || port < 1){
        printf("Usage: %s <port>\n", argv[0]);
        return 1;
    }
    servFd = socket(AF_INET, SOCK_STREAM, 0);
    sockaddr_in serverAddr{.sin_family=AF_INET, .sin_port=htons((short)port), .sin_addr={INADDR_ANY}};

    int res = bind(servFd, (sockaddr*) &serverAddr, sizeof(serverAddr));
    if(res) error(1, errno, "bind failed");

    res = listen(servFd, 1);
	if(res) error(1, errno, "listen failed");

    
    constexpr const int one = 1;
    setsockopt(servFd, SOL_SOCKET, SO_REUSEADDR, &one, sizeof(one));
    // epoll initialization
    int epollCr = epoll_create1(0); 
    epoll_event epollEvent;

    epollEvent.events = EPOLLIN;
    epollEvent.data.u64 = servFd;
    // adding server socket to epoll, so we can accept new connections
    epoll_ctl(epollCr, EPOLL_CTL_ADD, servFd, &epollEvent);
    initData();
    // main loop
    while(true){
        // waiting for events
        int epollWait = epoll_wait(epollCr, &epollEvent, 1, -1);
        // if we can accept new connection
        if (epollEvent.events & EPOLLIN && epollEvent.data.u64 == servFd){
            sockaddr_in clientAddr{0};
            socklen_t clientAddrLen = sizeof(clientAddr);

            int clientFd = accept(servFd, (sockaddr*)&clientAddr, &clientAddrLen);
            if(clientFd == -1){
                perror("accept failed");
                return 1;
            }
            // adding new client to set of clients
            clientFds.insert(clientFd);
            // adding new client to epoll
            epollEvent.events = EPOLLIN;
            epollEvent.data.u64 = clientFd;
            epoll_ctl(epollCr, EPOLL_CTL_ADD, clientFd, &epollEvent);
        }
        // if we have a message from client
        if (epollEvent.events & EPOLLIN && epollEvent.data.u64 != servFd){

            int cFd = (int) epollEvent.data.u64;
            std::cout << "Received message from client " << cFd << std::endl;
            char sizeBuf[2] {};
            if (recv(cFd, sizeBuf, 2, MSG_WAITALL) != 2){
                handleDisconnect((int) cFd);
                continue;
            }
            int size = getNumFromBuf(sizeBuf);
            char buf[size] {};
            if (recv(cFd, buf, size, MSG_WAITALL) != size){
                handleDisconnect(cFd);
                continue;
            }
            std::string msg(buf, size);
            if (msg.rfind("LOGIN", 0) == 0){
                if(handleLogin(msg.substr(6), cFd)){
                    char ok[2] {'O', 'K'};
                    send(cFd, ok, 2, 0);
                    playersNum++;
                }
                else {
                    char no[2] {'N', 'O'};
                    send(cFd, no, 2, 0);
                }
            }
            if (msg.rfind("ANS", 0) == 0){
                answers[cFd] = msg.substr(4);
                std::cout << answers[cFd] << std::endl;
            }
            if (msg == "ACTIVE:OK"){
                activePlayers++;
                if (activePlayers > 2 && activePlayers > playersNum/2){
                    gameStarted = true;
                    // start game
                }
            }
        }
    }
}


