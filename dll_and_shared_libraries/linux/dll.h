#ifdef BUILD_DLL
#define MINHADLL __declspec(dllexport)
#else
#define MINHADLL __declspec(dllimport)
#endif
MINHADLL int soma(int a,int b);
MINHADLL int sub(int a,int b);
