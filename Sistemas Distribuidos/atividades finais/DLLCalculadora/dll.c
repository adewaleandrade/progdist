#define BUILDDLL
#include <stdio.h>
#include "dll.h"

MINHADLL int soma(int a,int b)
{
    int res = a+b;
    return res;
}

MINHADLL int sub(int a,int b)
{
     int res = a-b;
     return res;
}

MINHADLL int multiplicacao(int a,int b)
{
    int res = a * b;
    return res;
}


MINHADLL float divisao(int a,int b)
{
    float res = (float)a/b;
    return res;
}
