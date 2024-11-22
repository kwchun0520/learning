#include <stdio.h>
typedef char* string;


char get_char(string prompt)
{
    char n;
    printf("%s\n", prompt);
    scanf("%s", &n);
    return n;
}

int main(void)
{
    char ans = get_char("Do you agree ?");
    if (ans=='Y' || ans=='y')
    {
        printf("Agreed\n");
    }
    else if (ans=='N' || ans=='n')
    {
        printf("Not agreed\n");
    }
}