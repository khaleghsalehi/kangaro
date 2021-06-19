
#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <time.h>

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


void revAString(char strg[]) {
    int g, numb;
    int tmpry = 0;

    for (numb = 0; strg[numb] != 0; numb++);
    for (g = 0; g < numb / 2; g++) {
        tmpry = strg[g];
        strg[g] = strg[numb - 1 - g];
        strg[numb - 1 - g] = tmpry;
    }
    for (g = 0; g < numb; g++)
        putchar(strg[g]);
}


JNIEXPORT jstring JNICALL
Java_ir_innovera_batsapp_security_Defender_getk(JNIEnv *env, jobject thiz) {

    srand(time(NULL));
    int i, num1 = 0, num2 = 0;
    int lower = 6, upper = 9;
    num1 = (rand() % (upper - lower + 1)) + lower;
    num2 = (rand() % (upper - lower + 1)) + lower;


    char str1[20];
    char str2[20];
    sprintf(str1, "%d", num1);
    sprintf(str2, "%d", num2);

    char part_1[num1];
    get_odd(part_1, sizeof part_1 - 1);

    char part_2[num2];
    get_evn(part_2, sizeof part_2 - 1);


    int sz = ((strlen(str1) + strlen(str2)) * 2) + 4 + 10 + 1;
    char final_token[sz]; // = malloc(((strlen(str1)+strlen(str2))*2)+3+1);
    strcpy(final_token, str1);
    //strcat(final_token,"-");
    strcat(final_token, str2);
    //  strcat(final_token,"-");
    strcat(final_token, part_1);

    char salt[10];
    get_mix(salt, 10 - 1);
    //  strcat(final_token,"-");
    strcat(final_token, salt);

    //  strcat(final_token,"-");
    strcat(final_token, part_2);


    return (*env)->NewStringUTF(env, final_token);
}


JNIEXPORT jstring JNICALL
Java_ir_innovera_batsapp_security_Defender_getAESKey(JNIEnv *env, jobject thiz) {
    unsigned char s[] =
            {
                    0x63, 0xe5, 0x4a, 0x81, 0xbc, 0x71, 0x6, 0xf2,
                    0x50, 0x82, 0x20, 0xbf, 0x6a, 0x5, 0x13, 0x65,
                    0xc, 0x46, 0xc7, 0xfd, 0x9f, 0x17, 0x9b, 0x4a,
                    0x45, 0xb4, 0xf, 0x10, 0xc0
            };

    for (unsigned int m = 0; m < sizeof(s); ++m) {
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

    return (*env)->NewStringUTF(env, s);
}


JNIEXPORT jstring JNICALL
Java_ir_innovera_batsapp_security_Defender_getSalt(JNIEnv *env, jobject thiz) {
    unsigned char s[] =
            {
                    0x4c, 0xba, 0x73, 0xd3, 0x64, 0x8c, 0x2a, 0x90,
                    0x5d, 0x80, 0x68, 0x75, 0x53, 0xae, 0x3d, 0xcc,
                    0x4b, 0x93, 0x17
            };

    for (unsigned int m = 0; m < sizeof(s); ++m) {
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

    return (*env)->NewStringUTF(env, s);
}


JNIEXPORT jstring JNICALL
Java_ir_innovera_batsapp_MainActivity_batsappStartCode(JNIEnv *env, jclass clazz) {
    unsigned char s[] =
            {

                    0x98, 0x9e, 0x70, 0x9e, 0x29, 0x21
            };

    for (unsigned int m = 0; m < sizeof(s); ++m) {
        unsigned char c = s[m];
        c = (c >> 0x2) | (c << 0x6);
        c = ~c;
        c += m;
        c = (c >> 0x7) | (c << 0x1);
        c -= m;
        c = (c >> 0x1) | (c << 0x7);
        c -= 0x3e;
        c = -c;
        c -= 0xe6;
        c = -c;
        c = (c >> 0x5) | (c << 0x3);
        c += m;
        c ^= m;
        c = -c;
        c ^= m;
        c -= m;
        c = -c;
        c += 0xab;
        c ^= 0x1e;
        c -= 0xa6;
        c = (c >> 0x5) | (c << 0x3);
        c += 0x44;
        c = -c;
        c -= 0x21;
        c = ~c;
        c = (c >> 0x2) | (c << 0x6);
        c -= 0xad;
        c = (c >> 0x2) | (c << 0x6);
        c -= m;
        c ^= m;
        c -= 0x29;
        c ^= m;
        s[m] = c;
    }
    return (*env)->NewStringUTF(env, s);
}

JNIEXPORT jstring JNICALL
Java_ir_innovera_batsapp_MainActivity_batsappStopCode(JNIEnv *env, jclass clazz) {
    unsigned char s[] =
            {

                    0x88, 0x6a, 0x6, 0xba, 0x45
            };

    for (unsigned int m = 0; m < sizeof(s); ++m) {
        unsigned char c = s[m];
        c ^= 0xa;
        c = (c >> 0x3) | (c << 0x5);
        c ^= 0xc2;
        c -= 0xe1;
        c ^= m;
        c -= m;
        c = (c >> 0x2) | (c << 0x6);
        c = -c;
        c -= 0x34;
        c = -c;
        c += m;
        c = -c;
        c ^= m;
        c = ~c;
        c ^= 0x61;
        c += m;
        c = ~c;
        c += 0x75;
        c = (c >> 0x6) | (c << 0x2);
        c -= m;
        c ^= 0xf2;
        c = -c;
        c += 0xd5;
        c = (c >> 0x1) | (c << 0x7);
        c ^= 0x12;
        c -= m;
        c = (c >> 0x3) | (c << 0x5);
        c = -c;
        c -= 0xa5;
        c ^= m;
        c -= m;
        c ^= m;
        s[m] = c;
    }

    return (*env)->NewStringUTF(env, s);
}

JNIEXPORT jstring JNICALL
Java_ir_innovera_batsapp_MainActivity_batsAppUpdateCode(JNIEnv *env, jclass clazz) {
    unsigned char s[] =
            {

                    0x19, 0x27, 0xa5, 0x17, 0x1f, 0xea, 0x28
            };

    for (unsigned int m = 0; m < sizeof(s); ++m) {
        unsigned char c = s[m];
        c = -c;
        c -= 0xe2;
        c ^= 0xde;
        c -= m;
        c = ~c;
        c = (c >> 0x1) | (c << 0x7);
        c ^= m;
        c = -c;
        c += 0xf7;
        c = ~c;
        c += 0x87;
        c = (c >> 0x3) | (c << 0x5);
        c -= 0x6;
        c ^= m;
        c = ~c;
        c = (c >> 0x3) | (c << 0x5);
        c += m;
        c = -c;
        c -= m;
        c = (c >> 0x6) | (c << 0x2);
        c = -c;
        c = ~c;
        c = -c;
        c += m;
        c ^= 0x5a;
        c = ~c;
        c -= m;
        c ^= 0xf5;
        c -= m;
        c = (c >> 0x3) | (c << 0x5);
        c -= 0x1c;
        c = (c >> 0x7) | (c << 0x1);
        s[m] = c;
    }

    return (*env)->NewStringUTF(env, s);
}

JNIEXPORT jstring JNICALL
Java_ir_innovera_batsapp_MainActivity_batsappREST(JNIEnv *env, jclass clazz) {
    unsigned char s[] =
            {

                    0x81, 0xe1, 0xa1, 0x80, 0xd8, 0xde, 0x63, 0xa3,
                    0x14, 0xcb, 0xc4, 0xa6, 0xde, 0x6e, 0x79, 0xbf,
                    0xdd, 0x8b
            };

    for (unsigned int m = 0; m < sizeof(s); ++m)
    {
        unsigned char c = s[m];
        c ^= 0x97;
        c = -c;
        c = (c >> 0x6) | (c << 0x2);
        c ^= m;
        c = (c >> 0x1) | (c << 0x7);
        c = ~c;
        c -= m;
        c = (c >> 0x3) | (c << 0x5);
        c -= 0x9e;
        c ^= 0xa8;
        c = -c;
        c = (c >> 0x1) | (c << 0x7);
        c -= 0x83;
        c ^= 0xe2;
        c = ~c;
        s[m] = c;
    }

    return (*env)->NewStringUTF(env, s);

}