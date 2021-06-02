
#include <string.h>
#include <jni.h>
#include <stdlib.h>

void get_odd(char *dest, size_t length) {
    char charset[] = "13579";
	srand(time(NULL));

    while (length-- > 0) {
        size_t index = (double) rand() / RAND_MAX * (sizeof charset - 1);
        *dest++ = charset[index];
    }
    *dest = '\0';
}


void get_evn(char *dest, size_t length) {
    char charset[] = "02468";
	srand(time(NULL));

    while (length-- > 0) {
        size_t index = (double) rand() / RAND_MAX * (sizeof charset - 1);
        *dest++ = charset[index];
    }
    *dest = '\0';
}

void get_mix(char *dest, size_t length) {
    char charset[] = "0123456789";
	srand(time(NULL));

    while (length-- > 0) {
        size_t index = (double) rand() / RAND_MAX * (sizeof charset - 1);
        *dest++ = charset[index];
    }
    *dest = '\0';
}



void revAString(char strg[])
{
    int g, numb;
    int tmpry = 0;

    for(numb=0; strg[numb] != 0; numb++);
    for(g = 0; g <numb/2; g++)
    {
        tmpry = strg[g];
        strg[g]=strg[numb - 1 - g];
        strg[numb - 1 - g] = tmpry;
    }
    for(g = 0; g < numb; g++)
        putchar(strg[g]);
}


JNIEXPORT jstring JNICALL
Java_com_example_batsapp_Security_getk( JNIEnv* env, jobject thiz )
{

	srand(time(NULL));
    int i,num1=0,num2=0;
    int lower = 6, upper = 9;
    num1 = (rand() %   (upper - lower + 1)) + lower;
    num2 = (rand() %   (upper - lower + 1)) + lower;


    char str1[20];
    char str2[20];
    sprintf(str1,"%d",num1);
    sprintf(str2,"%d",num2);

    char part_1[num1];
    get_odd(part_1, sizeof part_1 - 1);

    char part_2[num2];
    get_evn(part_2, sizeof part_2 - 1);


    int sz=((strlen(str1)+strlen(str2))*2)+4+10+1;
    char final_token[sz]; // = malloc(((strlen(str1)+strlen(str2))*2)+3+1);
    strcpy(final_token,str1);
    //strcat(final_token,"-");
    strcat(final_token,str2);
  //  strcat(final_token,"-");
    strcat(final_token,part_1);

    char salt[10];
    get_mix(salt,10-1);
  //  strcat(final_token,"-");
    strcat(final_token,salt);

  //  strcat(final_token,"-");
    strcat(final_token,part_2);


    return (*env)->NewStringUTF(env,final_token);
}



JNIEXPORT jstring JNICALL
Java_com_example_batsapp_Security_getAESKey( JNIEnv* env, jobject thiz )
{
      unsigned char s[] =
      {
          0x63, 0xe5, 0x4a, 0x81, 0xbc, 0x71, 0x6, 0xf2,
          0x50, 0x82, 0x20, 0xbf, 0x6a, 0x5, 0x13, 0x65,
          0xc, 0x46, 0xc7, 0xfd, 0x9f, 0x17, 0x9b, 0x4a,
          0x45, 0xb4, 0xf, 0x10, 0xc0
      };

      for (unsigned int m = 0; m < sizeof(s); ++m)
      {
          unsigned char c = s[m];
          c ^= 0x6d;
          c = (c >> 0x2) | (c << 0x6);
          c += m;
          c ^= 0x2d;
          c += m;
          c = -c;
          c += m;
          c ^= 0x7b;
          c -= 0x52;
          c ^= 0xd9;
          c = ~c;
          c = (c >> 0x2) | (c << 0x6);
          c += m;
          c ^= 0x68;
          c += 0x48;
          c = (c >> 0x5) | (c << 0x3);
          c ^= m;
          c = -c;
          c = (c >> 0x3) | (c << 0x5);
          c = ~c;
          c += 0x8;
          c = -c;
          c = ~c;
          c ^= 0x12;
          c -= m;
          c = (c >> 0x1) | (c << 0x7);
          c += m;
          c = ~c;
          c = -c;
          c += 0x76;
          c = ~c;
          c ^= 0xb4;
          s[m] = c;
      }

    return (*env)->NewStringUTF(env,  s );
}


JNIEXPORT jstring JNICALL
Java_com_example_batsapp_Security_getSalt( JNIEnv* env, jobject thiz )
{
unsigned char s[] =
    {
        0x4c, 0xba, 0x73, 0xd3, 0x64, 0x8c, 0x2a, 0x90,
        0x5d, 0x80, 0x68, 0x75, 0x53, 0xae, 0x3d, 0xcc,
        0x4b, 0x93, 0x17
    };

    for (unsigned int m = 0; m < sizeof(s); ++m)
    {
        unsigned char c = s[m];
        c = ~c;
        c = (c >> 0x6) | (c << 0x2);
        c -= 0xe0;
        c = (c >> 0x3) | (c << 0x5);
        c = -c;
        c += m;
        c = ~c;
        c += 0x70;
        c = ~c;
        c -= 0xac;
        c ^= 0x33;
        c = ~c;
        c += m;
        c = (c >> 0x6) | (c << 0x2);
        c ^= m;
        c = -c;
        c = (c >> 0x2) | (c << 0x6);
        c += 0xec;
        c = ~c;
        c += m;
        c = -c;
        c += 0xf8;
        c ^= 0x43;
        c = -c;
        c = ~c;
        c += m;
        c ^= 0x11;
        c = ~c;
        c ^= 0x9b;
        c = -c;
        c ^= m;
        c += 0xc5;
        s[m] = c;
    }
    return (*env)->NewStringUTF(env,  s );
}
