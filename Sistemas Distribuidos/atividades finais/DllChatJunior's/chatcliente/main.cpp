#include "chatcore.h"
#include <iostream>
#include <stdio.h>


using namespace std;

void receber(){
    system("cls");
    cout << "Sua mensagem eh:" << endl;
    Receber();
    system("pause");
}

void enviar(){
    char mensagem[255] = "";
    cout << "Insira sua mensagem abaixo:" << endl;
    fflush(stdin);
    gets(mensagem);
    Enviar(mensagem);
}

int main()
{
    char opcao;
    do{
        system("cls");
        cout << "Chat!" << endl;
        cout << "(1)Mandar mensagem." << endl;
        cout << "(2)Receber mensagem." << endl;
        cout << "(S)air do Programa." << endl;
        opcao = getchar();

        switch(opcao){
        case '2':
            receber();
            break;
        case '1':
            enviar();
            break;
        default:
            break;
        }
    }while(opcao != 's' && opcao != 'S');
    return 0;
}
