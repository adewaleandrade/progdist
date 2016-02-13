#ifdef BUILD_DLL
#define MINHADLL
#else
#define MINHADLL
#endif

/*
#define MINHADLL __declspec(dllexport)
#else
#define MINHADLL __declspec(dllimport)
#endif

*/
MINHADLL int soma(int a,int b);
MINHADLL int sub(int a,int b);
MINHADLL int multiplicacao (int a,int b);
MINHADLL float divisao(int a,int b);
