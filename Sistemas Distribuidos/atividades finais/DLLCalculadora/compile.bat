clear
gcc -c -DBUILD_DLL dll.c -o minhadll.o
gcc -shared -o libminhadll.so minhadll.o
gcc -L/. -Wall -o test proc.c -lminhadll
