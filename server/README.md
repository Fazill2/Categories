Server side of the game

Cmake is used to build the project. To build the project, run the following commands from the root of the project:

```bash
mkdir build
cd build
cmake ..
cmake --build .
```

To run the server, run the following command from the build directory:

```bash
./server <port>
```

Port is specified in the config file for every client so it is advised to use the same port as specified in the config file.
