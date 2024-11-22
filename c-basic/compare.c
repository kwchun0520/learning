#include <stdio.h>
typedef char* string;


int get_int(string prompt)
{
    int n;
    printf("%s\n", prompt);
    scanf("%i", &n);
    return n;
}


int main(void)
{

    int x = get_int("what is x ?");
    int y = get_int("what is y ?");
    
    if (x < y)
    {
        printf("x is less than y\n");
    }
    else if (x > y)
    {
        printf("x is greater than y\n");
    }
    else
    {
        printf("x is equal to y\n");
    }
}
