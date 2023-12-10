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

int servFd;

// client sockets
std::unordered_set<int> clientFds;

std::map<int, std::string> players;
std::map<int, int> points;



void handleDisconnect(int clientFd){
    clientFds.erase(clientFd);
    players.erase(clientFd);
    points.erase(clientFd);
    shutdown(clientFd, SHUT_RDWR);
    close(clientFd);
}

int getNumFromBuf(char buf[2]){
    int res = 10*((int) buf[0] - '0') + (buf[1]) - '0';
    return res;
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

    int epollCr = epoll_create1(0); 
    epoll_event epollEvent;

    epollEvent.events = EPOLLIN;
    epollEvent.data.u64 = servFd;
    epoll_ctl(epollCr, EPOLL_CTL_ADD, servFd, &epollEvent);

    while(true){
        int epollWait = epoll_wait(epollCr, &epollEvent, 1, -1);

        if (epollEvent.events & EPOLLIN && epollEvent.data.u64 == servFd){
            sockaddr_in clientAddr{0};
            socklen_t clientAddrLen = sizeof(clientAddr);

            int clientFd = accept(servFd, (sockaddr*)&clientAddr, &clientAddrLen);
            if(clientFd == -1){
                perror("accept failed");
                return 1;
            }

            clientFds.insert(clientFd);
            epollEvent.events = EPOLLIN;
            epollEvent.data.u64 = clientFd;
            epoll_ctl(epollCr, EPOLL_CTL_ADD, clientFd, &epollEvent);
        }
        if (epollEvent.events & EPOLLIN && epollEvent.data.u64 != servFd){
            char sizeBuf[2] {};
            if (recv(epollEvent.data.u64, sizeBuf, 2, MSG_WAITALL) != 2){
                std::cout << epollEvent.data.u64 << std::endl;
                handleDisconnect((int) epollEvent.data.u64);
                continue;
            }
            int size = getNumFromBuf(sizeBuf);
            std::cout << size << std::endl;
            char buf[size] {};
            if (recv(epollEvent.data.u64, buf, size, MSG_WAITALL) != size){
                handleDisconnect((int) epollEvent.data.u64);
                continue;
            }
            std::string msg(buf, size);
            // here converting to string does not work bcs it appends random chars to the string
            std::cout << msg << std::endl;
        }
    }
}


