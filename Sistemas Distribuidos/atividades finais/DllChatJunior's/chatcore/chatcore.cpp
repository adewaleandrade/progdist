#include <stdio.h>
#include "chatcore.h"

extern "C" DLL_EXPORT BOOL APIENTRY DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved)
{
    switch (fdwReason)
    {
        case DLL_PROCESS_ATTACH:
            // attach to process
            // return FALSE to fail DLL load
            break;

        case DLL_PROCESS_DETACH:
            // detach from process
            break;

        case DLL_THREAD_ATTACH:
            // attach to thread
            break;

        case DLL_THREAD_DETACH:
            // detach from thread
            break;
    }
    return TRUE; // succesful
}

void DLL_EXPORT Receber()
{
    FILE *file = fopen("msg.txt","r");
    char mensagem[200] = "";
    fgets(mensagem,200,file);
    printf("%s",mensagem);
}

void DLL_EXPORT Enviar(const char* msg)
{
 FILE *arquivo = fopen("msg.txt","w+");
 fprintf(arquivo,msg);
 fprintf(arquivo,"\n");
 fclose(arquivo);
}
