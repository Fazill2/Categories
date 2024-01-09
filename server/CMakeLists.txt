cmake_minimum_required(VERSION 3.8)
project(server)

set(CMAKE_CXX_STANDARD 14)



configure_file(answers/cities.txt
    ${CMAKE_CURRENT_BINARY_DIR} COPYONLY)
configure_file(answers/countries.txt
    ${CMAKE_CURRENT_BINARY_DIR} COPYONLY)
    configure_file(config.properties
    ${CMAKE_CURRENT_BINARY_DIR} COPYONLY)

add_executable(server main.cpp)
set_target_properties(${PROJECT_NAME} PROPERTIES OUTPUT_NAME "server.o")
