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
#include <algorithm>
#include <sys/signalfd.h>

class Player {
    public: int fd;
    public: std::string login;
    public: int points;
    public: std::string answer;
    public: double time;
    public: bool active;

    public: Player(){
        fd = -1;
        login = "";
        points = 0;
        answer = "";
        time = 0;
        active = false;
    };
};

struct Message {
    int size;
    int expectedSize;
    std::string msg;
    bool isSize; // true if we are reading size, false if we are reading message
};

// TODO: change some functions from using list of fds to using list of players

// global value for server file descriptor
int servFd;


int maxRounds = 10;
int currentRound = 0;
bool gameStarted = false;
bool gameEnded = false;
bool roundStarted = false;
int playersNum = 0;
int activePlayers = 0;
bool currentCategory = 0; // 0 - countries, 1 - cities
char currentLetter = 'A';
std::unordered_set<std::string> cities;
std::unordered_set<std::string> countries;

std::map<std::string, int> answers;
std::map<int, Message> currentMessages;
std::map<int, Player> currentPlayers;

void endGame();

// handles client disconnection
void handleDisconnect(int clientFd){
    std::cout << "disconnecting" << std::endl;
    currentPlayers.erase(clientFd);
    currentMessages.erase(clientFd);
    shutdown(clientFd, SHUT_RDWR);
    close(clientFd);
    playersNum--;
    activePlayers--;
    if (gameStarted && playersNum < 2){
        endGame();
    }
}

// converts char arr into an int
int getNumFromBuf(char buf[2]){
    int res = 10*((int) buf[0] - '0') + (buf[1]) - '0';
    return res;
}

// checks if login is in use, if not adds it to login map
bool handleLogin(std::string login, int clientFd){
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        if (i->second.login == login){
            return false;
        }
    }
    currentPlayers[clientFd].login = login;
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
    std::cout << "assigning points" << std::endl;
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        std::string ans = i->second.answer;
        std::transform(ans.begin(), ans.end(), ans.begin(),[](unsigned char c){ return std::tolower(c); });
       
        std::cout << ans << " " << countries.count(ans) << std::endl;
        answers.clear();
        int sumAnswers = 0;
        for (auto j = currentPlayers.begin(); j != currentPlayers.end(); j++){
            if (answers.count(j->second.answer) == 0){
                answers[j->second.answer] = 0;
            }
            answers[j->second.answer]++;
            sumAnswers++;
        }
        char lower = tolower(currentLetter);
        if (ans.rfind(lower, 0) == 0 && ((!currentCategory && countries.count(ans)) || (currentCategory && cities.count(ans)))){
            currentPlayers[i->first].points += (5 + 5*(sumAnswers- answers[ans]+1)/sumAnswers);
        }
    }
}

void endGame(){
    std::cout << "ending game" << std::endl;
    
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        send(i->second.fd, "07ENDGAME", 9, 0);
    }
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        for (auto j = currentPlayers.begin(); j != currentPlayers.end(); j++){
           char msg[128] {};
            // send message to each client with each players points as "<size>FPOINTS:<login>:<points>"
            sprintf(msg, "FPOINTS:%s:%d", j->second.login.c_str(), j->second.points);
            char finalMsg[256]{};
            sprintf(finalMsg, "%d%s", (int) strlen(msg), msg);
            send(i->second.fd, finalMsg, strlen(finalMsg), 0);
        }
    }
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        std::cout << i->first << " " << i->second.points << std::endl;
        i->second.active = false;
        i->second.answer = "";
        i->second.points = 0;
        i->second.time = 0;
    }
    gameStarted = false;
    gameEnded = true;
    activePlayers = 0;
    currentRound = 0;
    alarm(15);
}

void endRound(){
    std::cout << "ending round" << std::endl;
    assignPoints();
    char msg[256] {};
    const char* msgLen = (currentRound < 10) ? "05" : "06";
    sprintf(msg, "%sEND:%d", msgLen, currentRound);
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        send(i->second.fd, msg, strlen(msg), 0);
    }
    if (currentRound + 1 == maxRounds){
        endGame();
    }
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        std::cout << i->first << " " << i->second.points << std::endl;
    }
}

void startRound(){
    std::cout << "starting round" << std::endl;
    roundStarted = true;
    currentRound++;
    currentLetter = 'A' + rand()%26;
    currentCategory = rand()%2;
    char msg[256] {};
    int msgLen = (currentRound < 10) ? 11 : 12;
    sprintf(msg, "%dROUND:%d:%c:%d", msgLen, currentRound, currentLetter, currentCategory);
    for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
        i->second.answer = "";
        i->second.time = 0;
        if (i->second.active){
            send(i->second.fd, msg, strlen(msg), 0);
        }
    }
    alarm(15);
    std::cout << msg << std::endl;
}

void startGame(){
    std::cout << "starting game" << std::endl;
    gameStarted = true;
    startRound();
}

int handleActive(int clientFd){
    if (currentPlayers[clientFd].active){
        return 0;
    }
    currentPlayers[clientFd].active = true;
    activePlayers++;
    if (gameStarted){
        send(clientFd, "04WAIT", 6, 0);
    } else if (activePlayers >= 2 && activePlayers > playersNum/2){
        startGame();
    }
    return 0;
}


int main(int argc, char ** argv) {
    srand(time(NULL));
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
    // signal handling
    sigset_t mask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGALRM);
    if (sigprocmask(SIG_BLOCK, &mask, NULL) == -1)
        error(1, errno, "blocking failed");

    int sigFd = signalfd(-1, &mask, 0);
    epollEvent.events = EPOLLIN;
    epollEvent.data.u64 = sigFd;
    epoll_ctl(epollCr, EPOLL_CTL_ADD, sigFd, &epollEvent);
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
            Player p = Player();
            p.fd = clientFd;
            currentPlayers[clientFd] = p;
            // adding new client to epoll
            epollEvent.events = EPOLLIN;
            epollEvent.data.u64 = clientFd;
            epoll_ctl(epollCr, EPOLL_CTL_ADD, clientFd, &epollEvent);
        } else if (epollEvent.data.u64 == sigFd){
            struct signalfd_siginfo sfd_si;
            if (read(sigFd, &sfd_si, sizeof(sfd_si)) == -1)
                error(1, errno, "Error reading signal");
            if (gameStarted){
                endRound();
                if (!gameEnded){
                    startRound();
                }
            } else {
                for (auto i = currentPlayers.begin(); i != currentPlayers.end(); i++){
                    if (!i->second.active){
                        char msg[] = "07RESTART";
                        send(i->second.fd, msg, strlen(msg), 0);
                    }
                }
                gameEnded = false;
            }
        }
        // if we have a message from client
        else if (epollEvent.events & EPOLLIN && epollEvent.data.u64 != servFd && epollEvent.data.u64 != sigFd){

            int cFd = (int) epollEvent.data.u64;
            std::cout << "Received message from client " << cFd << std::endl;
            std::string msg;
            if (currentMessages.count(cFd) == 1){
                if (currentMessages[cFd].isSize == true){
                    std::cout << "incomplete size" << std::endl;
                    int remainingSize = currentMessages[cFd].expectedSize-currentMessages[cFd].size;
                    char sizeBuf[remainingSize] {};
                    int rcvSize = recv(cFd, sizeBuf, remainingSize, MSG_DONTWAIT);
                    if (rcvSize <= 0){
                        handleDisconnect((int) cFd);
                        continue;
                    } else if (rcvSize != remainingSize){
                        currentMessages[cFd].size += rcvSize;
                        currentMessages[cFd].msg += std::string(sizeBuf, rcvSize);
                        continue;
                    }
                    int size = std::stoi(currentMessages[cFd].msg + std::string(sizeBuf, rcvSize));
                    currentMessages.erase(cFd);
                    char buf[size] {};
                    rcvSize = recv(cFd, buf, size, MSG_DONTWAIT);
                    if (rcvSize <= 0){
                        handleDisconnect((int) cFd);
                        continue;
                    } else if (rcvSize != size){
                        Message msg = Message();
                        msg.expectedSize = size;
                        msg.size = rcvSize;
                        msg.msg = std::string(buf, rcvSize);
                        msg.isSize = false;
                        currentMessages[cFd] = msg;
                        continue;
                    }
                    msg = std::string(buf, size);
                } else{
                    std::cout << "incomplete msg" << std::endl;
                    int remainingSize = currentMessages[cFd].expectedSize-currentMessages[cFd].size;
                    char buf[remainingSize] {};
                    int rcvSize = recv(cFd, buf, remainingSize, MSG_DONTWAIT);
                    if (rcvSize <= 0){
                        handleDisconnect((int) cFd);
                        continue;
                    } else if (rcvSize != remainingSize){
                        currentMessages[cFd].size += rcvSize;
                        currentMessages[cFd].msg += std::string(buf, rcvSize);
                        continue;
                    }
                    msg = currentMessages[cFd].msg + std::string(buf, rcvSize);
                    currentMessages.erase(cFd);
                }
            } else{
                std::cout << "new msg" << std::endl;
                char sizeBuf[2] {};
                int rcvSize = recv(cFd, sizeBuf, 2, MSG_DONTWAIT);
                if (rcvSize <= 0){
                    handleDisconnect((int) cFd);
                    continue;
                } else if (rcvSize < 2){
                    Message msg = Message();
                    msg.expectedSize = 2;
                    msg.size = rcvSize;
                    msg.msg = std::string(sizeBuf, rcvSize);
                    msg.isSize = true;
                    currentMessages[cFd] = msg;
                    continue;
                }
                std::cout << "new msg" << std::endl;
                int size = getNumFromBuf(sizeBuf);
                std::cout << size << std::endl;
                char buf[size] {};
                rcvSize = recv(cFd, buf, size, MSG_DONTWAIT);
                if (rcvSize <= 0){
                    handleDisconnect((int) cFd);
                    continue;
                } else if (rcvSize < size){
                    Message msg = Message();
                    msg.expectedSize = size;
                    msg.size = rcvSize;
                    msg.msg = std::string(sizeBuf, rcvSize);
                    msg.isSize = false;
                    currentMessages[cFd] = msg;
                    continue;
                }
                msg = std::string(buf, rcvSize);
            }
            std::cout << msg << std::endl;
            if (msg.rfind("LOGIN", 0) == 0){
                if(handleLogin(msg.substr(6), cFd)){
                    char ok[4] {'0', '2', 'O', 'K'};
                    send(cFd, ok, 4, 0);

                    playersNum++;
                }
                else {
                    char no[4] {'0', '2','N', 'O'};
                    send(cFd, no, 4, 0);
                }
            }
            if (msg.rfind("ANS", 0) == 0){
                currentPlayers[cFd].answer = msg.substr(4);
                std::cout << currentPlayers[cFd].answer << std::endl;
            }
            if (msg == "ACTIVE:OK"){
                handleActive(cFd);
            }
        }
    }
}


