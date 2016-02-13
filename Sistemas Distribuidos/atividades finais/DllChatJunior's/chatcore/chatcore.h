#ifndef __CHATCORE_H__
#define __CHATCORE_H__
/*  To use this exported function of dll, include this header
 *  in your project.
 */

#include <windows.h>

#ifdef BUILD_DLL
    #define DLL_EXPORT __declspec(dllexport)
#else
    #define DLL_EXPORT __declspec(dllimport)
#endif


#ifdef __cplusplus
extern "C"
{
#endif

void DLL_EXPORT Enviar(const char* mensagem);
void DLL_EXPORT Receber();

#ifdef __cplusplus
}
#endif

#endif // __MAIN_H__
