#include<stdio.h>
#include<stdlib.h>

typedef int* triangular;

triangular newarray(int N);
int store(triangular as, int N, int row, int col, int val);
int fetch(triangular as, int N, int row, int col);


triangular newarray(int N)
{
    if (N > 0)
    {
    triangular pointer;

    int size;

    for (int i = 0; i < (N+1); i++)
    {
        size += i;
    }

    pointer = malloc( (size+1) * sizeof(int));

    *pointer = N;

    return pointer;

    }
    else{
        return NULL;
    }
    
}

int store(triangular as, int N, int row, int col, int val)
{
    if (*as != N)
    {
        return -1;
    }
    else if ( col < row  || col >= N || row >= N || col < 0 || row < 0)
    {
        return -1;
    }
    else
    {
        int N_size = N;
        int index = 0;

        for (int i = 0; i < row; i++)
        {
            index += N_size;
            N_size--;
        }
        
        index += col - row + 1;

        int *p;

        p = as + index;

        *p = val;
        
    }
}

int fetch(triangular as, int N, int row, int col)
{
    if (*as != N)
    {
        return -1;
    }
    else if ( col < row || col >= N || row >= N || col < 0 || row < 0 )
    {
        return -1;
    }
    else
    {

        int N_size = N;
        int index = 0;

        for (int i = 0; i < row; i++)
        {
            index += N_size;
            N_size--;
        }
        
        index += col - row + 1;

        return *(as + index);
    }
}