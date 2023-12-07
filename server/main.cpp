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

int servFd;

// client sockets
std::unordered_set<int> clientFds;

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
            printf("Connection from %s:%hu\n", inet_ntoa(clientAddr.sin_addr), ntohs(clientAddr.sin_port));

            clientFds.insert(clientFd);

            auto currTime = std::time(nullptr);
            char * text = std::ctime(&currTime);
            int count = write(clientFd, text, strlen(text));
            if(count != (int) strlen(text))
                perror("write failed");
            
            shutdown(clientFd, SHUT_RDWR);
            close(clientFd);
        }
    }
}
