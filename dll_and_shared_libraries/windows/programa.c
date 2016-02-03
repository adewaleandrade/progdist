#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <windows.h>
#include "dll.h"

typedef int (WINAPI * cfunc)(int v1, int v2);
cfunc FuncaoTemp;


int main(int argc,char **argv)
{
    int a,b,c;
    srand(time(NULL));
    a = (rand() % 100) + 1;
    b = (rand() % 100) + 1;
    
    HINSTANCE hLib=LoadLibrary("minhadll.dll");
    if (!hLib) { printf("error"); return 0; }
    FuncaoTemp=(cfunc)GetProcAddress((HMODULE)hLib, "soma");
    
    c = FuncaoTemp(a, b);
    FreeLibrary((HMODULE)hLib); 

    printf("\nA soma de %d + %d = %d\n",a,b,c);
    system("pause");
}
