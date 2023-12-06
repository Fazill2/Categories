#include <cstdio>
#include <cstdlib>
#include <ctime>
#include <cstring>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

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
    
    sockaddr_in myAddr {};
    myAddr.sin_family = AF_INET;
    myAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    myAddr.sin_port = htons((uint16_t)port);
    
    int fd = socket(PF_INET, SOCK_STREAM, 0);
    if(fd == -1){
        perror("socket failed");
        return 1;
    }
    
    constexpr const int one = 1;
    setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &one, sizeof(one));
 
    int fail = bind(fd, (sockaddr*) &myAddr, sizeof(myAddr));
    if(fail){
        perror("bind failed");
        return 1;
    }
    
    fail = listen(fd, 1);
    if(fail){
        perror("listen failed");
        return 1;
    }
    
    while(true){
        sockaddr_in clientAddr;
        socklen_t clientAddrLen = sizeof(clientAddr);
        int clientFd = accept(fd, (sockaddr*)&clientAddr, &clientAddrLen);
        if(clientFd == -1){
            perror("accept failed");
            return 1;
        }
        
        printf("Connection from %s:%hu\n", inet_ntoa(clientAddr.sin_addr), ntohs(clientAddr.sin_port));

        auto currTime = std::time(nullptr);
        char * text = std::ctime(&currTime);
        
        int count = write(clientFd, text, strlen(text));
        if(count != (int) strlen(text))
            perror("write failed");
        
        shutdown(clientFd, SHUT_RDWR);
        close(clientFd);
    }
}
