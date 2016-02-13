#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include "dll.h"

int main(int argc,char **argv)
{
 int a,b;
 srand(time(NULL));
 a = (rand() % 100) + 1;
 b = (rand() % 100) + 1;
 printf("\nA soma de %d + %d = %d\n",a,b,soma(a,b));
 
 a = (rand() % 100) + 1;
 b = (rand() % 100) + 1;
 printf("\nA subtracao de %d - %d = %d\n",a,b,sub(a,b));

 a = (rand() % 100) + 1;
 b = (rand() % 100) + 1;
 printf("\nA multiplicacao de %d - %d = %d\n",a,b,multiplicacao(a,b)); 

 a = (rand() % 100) + 1;
 b = (rand() % 100) + 1;
 printf("\nA divisao de %d - %d = %.2f\n",a,b,divisao(a,b)); 

 system("pause");
 return 0;
}
