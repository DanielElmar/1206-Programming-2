#include<stdio.h>
#include<math.h>

int scan(int *integral , double *decimal, double d);

int scan(int *integral , double *decimal, double d)
{
	int int_part;
	double abs_d;

	abs_d = fabs(d);
	int_part = floor(abs_d);

	*integral =  int_part;
	*decimal = abs_d - int_part;

	if( d >= 0 )
	{
	return 1;
	}
	else
	{
	return -1;
	}	

}
