#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <sstream>
#include <math.h>
#include <cmath>
#include "../XMLStyle/tinystr.h"
#include "../XMLStyle/tinyxml.h"
#include <map>
#include <cstdlib>
#include <time.h>
#define efsilon 0.05

using namespace std;




class Color {
  public:
      Color() { r = 0; g = 0; b = 0;}
      Color(float r1, float g1, float b1) : r(r1), g(g1), b(b1) {}
      float r;
      float g;
      float b;

      Color operator+(const Color & other) {
          return Color(this->r+other.r, this->g+other.g, this->b+other.b);
      }

};

class Material {
  public:
    Material() {  }
    ~Material() {  }

    int materialIndex;

    Color amb;
    Color dif;
    Color spe;
    float specExpP;
    Color refl;
    float refractionIndex;
};

class Vec {
  public:
    Vec() {}
    Vec(float x1, float y1, float z1) : x(x1), y(y1), z(z1) {}
    float x;
    float y;
    float z;

    float len() {
        return sqrt(x*x+y*y+z*z);
    }

    Vec operator+(const Vec & other) {
        return Vec(this->x+other.x, this->y+other.y, this->z+other.z);
    }

    Vec operator-(const Vec & other) {
        return Vec(this->x-other.x, this->y-other.y, this->z-other.z);
    }

    Vec operator=(const Vec & other) {
        this->x = other.x;
        this->y = other.y;
        this->z = other.z;
        return *this;
    }



};

Vec crossProduct(Vec v1, Vec v2) {
    Vec vRes;
    vRes.x=(v1.y*v2.z) - (v1.z*v2.y);
    vRes.y=(v1.z*v2.x) - (v1.x*v2.z);
    vRes.z=(v1.x*v2.y) - (v1.y*v2.x);

    return vRes;
}


Vec vecDiv(Vec v, float k) {
    return Vec(v.x/k, v.y/k, v.z/k);
}

class Ray {
public:
    Ray() {};
    Ray(Vec p, Vec dir) {
        this->p = p;
        this->dir = dir;
    }

    Vec p;
    Vec dir;
};



Vec vecMul(Vec v, float t) {
    return Vec(v.x*t,v.y*t,v.z*t);
}

Color colMul(Color c, float t) {
    return Color(c.r*t,c.g*t,c.b*t);
}

class Triangle {
  public:
    Triangle() {
        
    }
    Triangle(Vec p1, Vec p2, Vec p3, Material *matP) : pos1(p1), pos2(p2), pos3(p3), materialP(matP) {
        preProcess();
    }
    ~Triangle() {  }

    void preProcess() {

        n=crossProduct((pos2-pos1),(pos3-pos1));
        n = vecMul(n,(1/n.len()));

        aeb = pos1-pos2;
        bec = pos2-pos3;
        cea = pos3-pos1;
        bea = pos2-pos1;
        ceb = pos3-pos2;
        aec = pos1-pos3;

        aab = pos1+pos2;
        bac = pos2+pos3;
        caa = pos3+pos1;
    }



    int id;

    Vec pos1;
    Vec pos2;
    Vec pos3;
    int matInd;
    Material *materialP;
    Vec n;

    Vec aeb;
    Vec bec;
    Vec cea;
    Vec bea;
    Vec ceb;
    Vec aec;

    Vec aab;
    Vec bac;
    Vec caa;
};

class Sphere {
  public:
    Sphere() {  }
    Sphere(Vec p1, float rad, int matInd) : center(p1), radius(rad), matInd(matInd) {  }
    ~Sphere() { }
    Vec center;
    float radius;
    int matInd;
    Material *materialP;
    int id;
};

class PointLight {
  public:
    PointLight() {  }
    ~PointLight() { }
    Vec pos;
    Color I;

    int id;
};

class Camera {
  public:
      Camera() { };
      ~Camera() {  };

      Vec pos;
      Vec gaze;
      Vec up;
      float left;
      float right;
      float bottom;
      float top;
      float distance;

      int id;

      int HorRes, VerRes;

      string outputName;

};

class TS {
public:
    TS() {};
    ~TS() {};
    Triangle *trip;
    Sphere *sphp;
};

class interReturn {
public:
    interReturn() { doesinter=false;}
    ~interReturn() {}

    bool doesinter;
    float t;
    Vec intP;
    TS pointerClosest;
};

class planeEq2 {
public:
    planeEq2(Vec p1, Vec n1) {
        n=n1;
        p=p1;
        d= -1 * ((p1.x)*(n1.x) + (p1.y)*(n1.y) + (p1.z)*(n1.z));
    }
    ~planeEq2() {}
    Vec n;
    Vec p;
    float d;
};



// declerations
ifstream inFile;

int rayReflectCount;

map<int, Material*> materialMap;
vector<Triangle*> triangleVector;
vector<Sphere*> sphereVector;
vector<PointLight*> pointLightVector;
Color ambientLight;
Color backgroundColor;
vector<Camera*> cameras;

map<int, Vec*> vertexMap;


Vec g,o,u,v,w;
Vec zeroVec(0,0,0);

float pixelW, pixelH;
float d;

float leftP,rightP,topP,bottomP;

Vec midd, middLeft, topLeft;

Vec firstPixel;

Vec horizontalIncrement, verticalIncrement;

Ray currentRay;

Color **imageArray;

void readColor(Color &c) {
    inFile >> c.r;
    inFile >> c.g;
    inFile >> c.b;
}



void readVec(Vec &v) {
    inFile >> v.x;
    inFile >> v.y;
    inFile >> v.z;
}



bool readSceneFile(char *fileName) {

    TiXmlDocument doc( fileName );
    doc.LoadFile();

    if(doc.LoadFile())
    {
        TiXmlElement *pRoot, *pParm, *pInner;
        pRoot = doc.FirstChildElement("Scene");
        if(pRoot)
        {
            pParm = pRoot->FirstChildElement();
            int intValue;
            const char * value = pParm->Value() ;
            while(pParm)
            {
                value = pParm->Value();
                if (strcmp(pParm->Value(), "Material") == 0) {
                    Material *mater = new Material();

                    {
                        int ind;
                        stringstream strValue;
                        strValue << pParm->Attribute("id");
                        strValue >> ind;
                        materialMap[ind] = mater;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Ambient")->GetText();
                        strValue >> mater->amb.r;
                        strValue >> mater->amb.g;
                        strValue >> mater->amb.b;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Diffuse")->GetText();
                        strValue >> mater->dif.r;
                        strValue >> mater->dif.g;
                        strValue >> mater->dif.b;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Specular")->GetText();
                        strValue >> mater->spe.r;
                        strValue >> mater->spe.g;
                        strValue >> mater->spe.b;
                        strValue >> mater->specExpP;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Reflectance")->GetText();
                        strValue >> mater->refl.r;
                        strValue >> mater->refl.g;
                        strValue >> mater->refl.b;
                    }


                }
                else if(strcmp(pParm->Value(), "Vertex") == 0) {

                    Vec *v = new Vec();

                    {
                        stringstream strValue;
                        strValue << pParm->Attribute("id");
                        strValue >> intValue;
                        vertexMap[intValue] = v;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Coordinates")->GetText();
                        strValue >> v->x;
                        strValue >> v->y;
                        strValue >> v->z;
                    }


                }

                else if(strcmp(pParm->Value(), "Triangle") == 0) {
                    Triangle *tri = new Triangle();
                    triangleVector.push_back(tri);

                    {
                        stringstream strValue;
                        strValue << pParm->Attribute("id");
                        strValue >> tri->id;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Vertices")->GetText();
                        strValue >> intValue;
                        tri->pos1 = *vertexMap[intValue];
                        strValue >> intValue;
                        tri->pos2 = *vertexMap[intValue];
                        strValue >> intValue;
                        tri->pos3 = *vertexMap[intValue];
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("MaterialId")->GetText();
                        strValue >> tri->matInd;
                    }
                    tri->preProcess();
                }

                else if(strcmp(pParm->Value(), "Sphere") == 0) {
                    Sphere *sph = new Sphere();
                    sphereVector.push_back(sph);

                    {
                        stringstream strValue;
                        strValue << pParm->Attribute("id");
                        strValue >> sph->id;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Center")->GetText();
                        strValue >> intValue;
                        sph->center = *vertexMap[intValue];
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Radius")->GetText();
                        strValue >> sph->radius;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("MaterialId")->GetText();
                        strValue >> sph->matInd;
                    }
                }

                else if(strcmp(pParm->Value(), "PointLight") == 0) {
                    PointLight *pl = new PointLight();
                    pointLightVector.push_back(pl);

                    {
                        stringstream strValue;
                        strValue << pParm->Attribute("id");
                        strValue >> pl->id;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Position")->GetText();
                        strValue >> pl->pos.x;
                        strValue >> pl->pos.y;
                        strValue >> pl->pos.z;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Intensity")->GetText();
                        strValue >> pl->I.r;
                        strValue >> pl->I.g;
                        strValue >> pl->I.b;
                    }

                }

                else if(strcmp(pParm->Value(), "AmbientLight") == 0) {

                    {
                        stringstream strValue;
                        strValue << pParm->GetText();
                        strValue >> ambientLight.r;
                        strValue >> ambientLight.g;
                        strValue >> ambientLight.b;
                    }

                }

                else if(strcmp(pParm->Value(), "BackgroundColor") == 0) {

                    {
                        stringstream strValue;
                        strValue << pParm->GetText();
                        strValue >> backgroundColor.r;
                        strValue >> backgroundColor.g;
                        strValue >> backgroundColor.b;
                    }

                }

                else if(strcmp(pParm->Value(), "BackgroundColor") == 0) {

                    {
                        stringstream strValue;
                        strValue << pParm->GetText();
                        strValue >> backgroundColor.r;
                        strValue >> backgroundColor.g;
                        strValue >> backgroundColor.b;
                    }

                }

                else if(strcmp(pParm->Value(), "RayReflectionCount") == 0) {

                    {
                        stringstream strValue;
                        strValue << pParm->GetText();
                        strValue >> rayReflectCount;
                    }

                }

                else if(strcmp(pParm->Value(), "Camera") == 0) {
                    Camera *cmr = new Camera();
                    cameras.push_back(cmr);

                    {
                        stringstream strValue;
                        strValue << pParm->Attribute("id");
                        strValue >> cmr->id;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Position")->GetText();
                        strValue >> cmr->pos.x;
                        strValue >> cmr->pos.y;
                        strValue >> cmr->pos.z;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Gaze")->GetText();
                        strValue >> cmr->gaze.x;
                        strValue >> cmr->gaze.y;
                        strValue >> cmr->gaze.z;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("Up")->GetText();
                        strValue >> cmr->up.x;
                        strValue >> cmr->up.y;
                        strValue >> cmr->up.z;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("ImagePlane")->GetText();
                        strValue >> cmr->left;
                        strValue >> cmr->right;
                        strValue >> cmr->bottom;
                        strValue >> cmr->top;
                        strValue >> cmr->distance;
                        strValue >> cmr->HorRes;
                        strValue >> cmr->VerRes;
                    }

                    {
                        stringstream strValue;
                        strValue << pParm->FirstChildElement("OutputName")->GetText();
                        strValue >> cmr->outputName;
                    }

                }


                pParm = pParm->NextSiblingElement();
            }
        }

        return true;
    }
    return false;

 
}

void deAllocate() {
//    while(!materialMap.empty()) {
//        delete materialMap.back();
//        materialMap.pop_back();
//    }
    
    materialMap.erase(materialMap.begin(), materialMap.end());
    vertexMap.erase(vertexMap.begin(), vertexMap.end());
    triangleVector.erase(triangleVector.begin(), triangleVector.end());
    sphereVector.erase(sphereVector.begin(), sphereVector.end());
    pointLightVector.erase(pointLightVector.begin(), pointLightVector.end());
    cameras.erase(cameras.begin(), cameras.end());
}




float det3x3(float m[3][3]) {

    float result=0;
    result += (m[0][0] * m[1][1]) * m[2][2];
    result += (m[1][0] * m[2][1]) * m[0][2];
    result += (m[2][0] * m[0][1]) * m[1][2];

    result -= (m[0][2] * m[1][1]) * m[2][0];
    result -= (m[1][2] * m[2][1]) * m[0][0];
    result -= (m[2][2] * m[0][1]) * m[1][0];

    return result;

}


Vec toUnit(Vec v) {
    float length = sqrt(v.x*v.x + v.y*v.y + v.z*v.z);
    v.x /= length;
    v.y /= length;
    v.z /= length;
    return v;
}

float triArea (Vec p1, Vec p2, Vec p3) {
    return ((crossProduct((p2-p1) , (p3-p1))).len()/2);
}

bool sd(Vec v1, Vec v2) {
 //  return ( ( (v1.x>0 && v2.x>0) || (v1.x<0 && v2.x<0) || (v1.x==0 && v2.x==0) ) && ( (v1.y>0 && v2.y>0) || (v1.y<0 && v2.y<0) || (v1.y==0 && v2.y==0) )  && ( (v1.z>0 && v2.z>0) || (v1.z<0 && v2.z<0) || (v1.z==0 && v2.z==0) ) );
     return ( ( (v1.x>=0 && v2.x>=0) || (v1.x<=0 && v2.x<=0) ) && ( (v1.y>=0 && v2.y>=0) || (v1.y<=0 && v2.y<=0) )  && ( (v1.z>=0 && v2.z>=0) || (v1.z<=0 && v2.z<=0) ) );
}

float dotP(Vec v1, Vec v2) {
      return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z) ;
}



interReturn findClosestInt(Ray r, float len, bool is, bool isTri, int id) {

    interReturn ir;

    Triangle* trp;
    Sphere *spp;

    float tclosest;
    bool intExists = false;



    int index=0;
    int tlen=(triangleVector).size();
    int slen=(sphereVector).size();

    float delta;
    float aa;
    float bb;
    float cc;




    Vec interPoint;
    float interT;

    // triangle lari gez
    for(index=0; index<tlen; index++) {
        trp=(triangleVector).at(index);
        if(is && isTri && trp->id == id) {
            continue;
        }
        //continue with the next element if the ray is parallel to the plane
        if(dotP(r.dir, trp->n)==0) {
            continue;
        }




        float m1[3][3];
        float m2[3][3];
        float m3[3][3];
        float m4[3][3];

        Vec a = trp->pos1;
        Vec b = trp->pos2;
        Vec c = trp->pos3;
        Vec d = r.dir;
        Vec oo = r.p;

        m1[0][0] = a.x -b.x;
        m1[0][1] = a.x -c.x;
        m1[0][2] = d.x;

        m1[1][0] = a.y-b.y;
        m1[1][1] = a.y-c.y;
        m1[1][2] = d.y;

        m1[2][0] = a.z-b.z;
        m1[2][1] = a.z-c.z;
        m1[2][2] = d.z;



        m2[0][0] = a.x-oo.x;
        m2[0][1] = a.x-c.x;
        m2[0][2] = d.x;

        m2[1][0] = a.y-oo.y;
        m2[1][1] = a.y-c.y;
        m2[1][2] = d.y;

        m2[2][0] = a.z-oo.z;
        m2[2][1] = a.z-c.z;
        m2[2][2] = d.z;



        m3[0][0] = a.x-b.x;
        m3[0][1] = a.x-oo.x;
        m3[0][2] = d.x;

        m3[1][0] = a.y-b.y;
        m3[1][1] = a.y-oo.y;
        m3[1][2] = d.y;

        m3[2][0] = a.z-b.z;
        m3[2][1] = a.z-oo.z;
        m3[2][2] = d.z;


        m4[0][0] = a.x-b.x;
        m4[0][1] = a.x-c.x;
        m4[0][2] = a.x-oo.x;

        m4[1][0] = a.y-b.y;
        m4[1][1] = a.y-c.y;
        m4[1][2] = a.y-oo.y;

        m4[2][0] = a.z-b.z;
        m4[2][1] = a.z-c.z;
        m4[2][2] = a.z-oo.z;

        float det1 = det3x3(m1);
        float beta = det3x3(m2)/det1;
        float gama = det3x3(m3)/det1;
        float ti = det3x3(m4)/det1;
        float alpha = 1-beta-gama;


        interT = ti;
        interPoint = (r.p + vecMul(r.dir , interT));



        if(ti>len && alpha<1 && alpha>0 && beta<1 && beta>0 && gama<1 && gama>0) {
            if(!intExists || interT<tclosest) {
                intExists = true;
                ir.doesinter=true;
                tclosest=interT;
                ir.t=tclosest;
                ir.intP=interPoint;
                (ir.pointerClosest).trip=trp;
                (ir.pointerClosest).sphp=NULL;
            }
        }



    }

    // sphere leri gez
    Vec ominusc;
    float ttemp;
    float rad;
    for(index=0; index<slen; index++) {

        spp=sphereVector.at(index);
        if(is && !isTri && spp->id == id) {
            continue;
        }
        rad=spp->radius;
        ominusc=(r.p-spp->center);

        bb=2*dotP(ominusc,r.dir);
        aa=dotP(r.dir,r.dir);
        cc=dotP(ominusc,ominusc) - rad*rad;
        delta=bb*bb - 4*aa*cc;

        if(delta<0) {
            continue;
        }

        if(delta>0) {
            float temp1 = ((-bb - sqrt(delta)) / (2*aa));
            float temp2 = ((-bb + sqrt(delta)) / (2*aa));
            ttemp = min(temp1,temp2);
            if((!intExists || ttemp<tclosest) && ttemp>len) {
                intExists = true;
                tclosest=ttemp;
                ir.intP=(r.p + vecMul(r.dir , tclosest));
                ir.doesinter=true;
                ir.t=tclosest;
                (ir.pointerClosest).trip=NULL;
                (ir.pointerClosest).sphp=spp;
            }

        }
        if(delta==0) {
            ttemp=(-bb ) / (2*aa);
            if((!intExists || ttemp<tclosest) && ttemp>len) {
                intExists = true;
                tclosest=ttemp;
                ir.intP=(r.p + vecMul(r.dir , tclosest));
                ir.doesinter=true;
                ir.t=tclosest;
                (ir.pointerClosest).trip=NULL;
                (ir.pointerClosest).sphp=spp;
            }
        }
    }

    return ir;
}

bool isThereAnObjectOnTheWayTri(Ray r, Triangle *tri) {


    Triangle* trp;
    Sphere *spp;





    int index=0;
    int tlen=(triangleVector).size();
    int slen=(sphereVector).size();

    float delta;
    float aa;
    float bb;
    float cc;


    Vec interPoint;
    float interT;

    // triangle lari gez
    for(index=0; index<tlen; index++) {

        trp=(triangleVector).at(index);
        if(trp->id == tri->id) {
            continue;
        }
        //continue with the next element if the ray is parallel to the plane
        if(dotP(r.dir, trp->n)==0) {
            continue;
        }



        float m1[3][3];
        float m2[3][3];
        float m3[3][3];
        float m4[3][3];

        Vec a = trp->pos1;
        Vec b = trp->pos2;
        Vec c = trp->pos3;
        Vec d = r.dir;
        Vec oo = r.p;

        m1[0][0] = a.x -b.x;
        m1[0][1] = a.x -c.x;
        m1[0][2] = d.x;

        m1[1][0] = a.y-b.y;
        m1[1][1] = a.y-c.y;
        m1[1][2] = d.y;

        m1[2][0] = a.z-b.z;
        m1[2][1] = a.z-c.z;
        m1[2][2] = d.z;



        m2[0][0] = a.x-oo.x;
        m2[0][1] = a.x-c.x;
        m2[0][2] = d.x;

        m2[1][0] = a.y-oo.y;
        m2[1][1] = a.y-c.y;
        m2[1][2] = d.y;

        m2[2][0] = a.z-oo.z;
        m2[2][1] = a.z-c.z;
        m2[2][2] = d.z;



        m3[0][0] = a.x-b.x;
        m3[0][1] = a.x-oo.x;
        m3[0][2] = d.x;

        m3[1][0] = a.y-b.y;
        m3[1][1] = a.y-oo.y;
        m3[1][2] = d.y;

        m3[2][0] = a.z-b.z;
        m3[2][1] = a.z-oo.z;
        m3[2][2] = d.z;


        m4[0][0] = a.x-b.x;
        m4[0][1] = a.x-c.x;
        m4[0][2] = a.x-oo.x;

        m4[1][0] = a.y-b.y;
        m4[1][1] = a.y-c.y;
        m4[1][2] = a.y-oo.y;

        m4[2][0] = a.z-b.z;
        m4[2][1] = a.z-c.z;
        m4[2][2] = a.z-oo.z;

        float det1 = det3x3(m1);
        float beta = det3x3(m2)/det1;
        float gama = det3x3(m3)/det1;
        float ti = det3x3(m4)/det1;
        float alpha = 1-beta-gama;


        interT = ti;
        interPoint = (r.p + vecMul(r.dir , interT));

        if(ti>0 && ti<1 && alpha<1 && alpha>0 && beta<1 && beta>0 && gama<1 && gama>0) {
                return true;
        }


    }

    // sphere leri gez
    Vec ominusc;
    float ttemp;
    float rad;
    for(index=0; index<slen; index++) {

        spp=sphereVector.at(index);
        rad=spp->radius;
        ominusc=(r.p-spp->center);

        bb=2*dotP(ominusc,r.dir);
        aa=dotP(r.dir,r.dir);
        cc=dotP(ominusc,ominusc) - rad*rad;
        delta=bb*bb - 4*aa*cc;

        if(delta<0) {
            continue;
        }

        if(delta>0) {
            float temp1 = ((-bb - sqrt(delta)) / (2*aa));
            float temp2 = ((-bb + sqrt(delta)) / (2*aa));
            if((temp1>0 && temp1<1) || (temp2>0 && temp2<1)) {
                return true;
            }

        }
        if(delta==0) {
            ttemp=(-bb ) / (2*aa);
            if((ttemp>0 && ttemp<1)) {
                return true;
            }
        }
    }

    return false;
}

bool isThereAnObjectOnTheWaySph(Ray r, Sphere *sph) {


    Triangle* trp;
    Sphere *spp;





    int index=0;
    int tlen=(triangleVector).size();
    int slen=(sphereVector).size();

    float delta;
    float aa;
    float bb;
    float cc;


    Vec interPoint;
    float interT;

    // triangle lari gez
    for(index=0; index<tlen; index++) {

        trp=(triangleVector).at(index);
        //continue with the nex element if the ray is parallel to the plane
        if(dotP(r.dir, trp->n)==0) {
            continue;
        }



        float m1[3][3];
        float m2[3][3];
        float m3[3][3];
        float m4[3][3];

        Vec a = trp->pos1;
        Vec b = trp->pos2;
        Vec c = trp->pos3;
        Vec d = r.dir;
        Vec oo = r.p;

        m1[0][0] = a.x -b.x;
        m1[0][1] = a.x -c.x;
        m1[0][2] = d.x;

        m1[1][0] = a.y-b.y;
        m1[1][1] = a.y-c.y;
        m1[1][2] = d.y;

        m1[2][0] = a.z-b.z;
        m1[2][1] = a.z-c.z;
        m1[2][2] = d.z;



        m2[0][0] = a.x-oo.x;
        m2[0][1] = a.x-c.x;
        m2[0][2] = d.x;

        m2[1][0] = a.y-oo.y;
        m2[1][1] = a.y-c.y;
        m2[1][2] = d.y;

        m2[2][0] = a.z-oo.z;
        m2[2][1] = a.z-c.z;
        m2[2][2] = d.z;



        m3[0][0] = a.x-b.x;
        m3[0][1] = a.x-oo.x;
        m3[0][2] = d.x;

        m3[1][0] = a.y-b.y;
        m3[1][1] = a.y-oo.y;
        m3[1][2] = d.y;

        m3[2][0] = a.z-b.z;
        m3[2][1] = a.z-oo.z;
        m3[2][2] = d.z;


        m4[0][0] = a.x-b.x;
        m4[0][1] = a.x-c.x;
        m4[0][2] = a.x-oo.x;

        m4[1][0] = a.y-b.y;
        m4[1][1] = a.y-c.y;
        m4[1][2] = a.y-oo.y;

        m4[2][0] = a.z-b.z;
        m4[2][1] = a.z-c.z;
        m4[2][2] = a.z-oo.z;

        float det1 = det3x3(m1);
        float beta = det3x3(m2)/det1;
        float gama = det3x3(m3)/det1;
        float ti = det3x3(m4)/det1;
        float alpha = 1-beta-gama;


        interT = ti;
        interPoint = (r.p + vecMul(r.dir , interT));

        if(ti>0 && ti<1 && alpha<1 && alpha>0 && beta<1 && beta>0 && gama<1 && gama>0) {
            return true;
        }

    }

    // sphere leri gez
    Vec ominusc;
    float ttemp;
    float rad;
    for(index=0; index<slen; index++) {

        spp=sphereVector.at(index);
        if(spp->id == sph->id) {
            continue;
        }
        rad=spp->radius;
        ominusc=(r.p-spp->center);

        bb=2*dotP(ominusc,r.dir);
        aa=dotP(r.dir,r.dir);
        cc=dotP(ominusc,ominusc) - rad*rad;
        delta=bb*bb - 4*aa*cc;

        if(delta<0) {
            continue;
        }

        if(delta>0) {
            float temp1 = ((-bb - sqrt(delta)) / (2*aa));
            float temp2 = ((-bb + sqrt(delta)) / (2*aa));
            if((temp1>0 && temp1<1) || (temp2>0 && temp2<1)) {
                return true;
            }

        }
        if(delta==0) {
            ttemp=(-bb ) / (2*aa);
            if((ttemp>0 && ttemp<1)) {
                return true;
            }
        }
    }

    return false;
}

float max(float f1, float f2) {
    if(f1>f2) {
        return f1;
    }
    return f2;
}

float min(float f1, float f2) {
    if(f1<f2) {
        return f1;
    }
    return f2;
}

Color componentwiseMultiplication(Color c1, Color c2) {
    return Color(c1.r*c2.r, c1.g*c2.g, c1.b*c2.b);
}

//Color calculateDiffuseShadingTri(Triangle *tri, Material *material, Vec p) {
//    Color result(0,0,0);
//    Ray *r = new Ray();
//    r->p = p;
//    float cos;
//    Vec n = tri->n;
//    bool is;
//    for(int i=0; i < pointLightVector.size(); i++) {
//        r->dir = pointLightVector.at(i)->pos - p;
//        is = isThereAnObjectOnTheWayTri(*r, tri);
//        if(!is) {
//          // arada cisim yoksa
//            cos = max(0,dotP(n,toUnit(r->dir)));
//            result = result + colMul(pointLightVector.at(i)->I, cos);
//        }
//    }
//    result = componentwiseMultiplication(material->dif, result);
//    return result;
//}

Vec tersCevir(Vec v) {
    v.x = -1 * v.x;
    v.y = -1 * v.y;
    v.z = -1 * v.z;
    return v;
}

//Color calculateDiffuseShadingSph(Sphere *sph, Material *material, Vec p) {
//    Color result(0,0,0);
//    Ray *r = new Ray();
//    r->p = p;
//    float cos;
//    Vec n = toUnit(p - sph->center);
//    bool is;
//    for(int i=0; i < pointLightVector.size(); i++) {
//        r->dir = pointLightVector.at(i)->pos - p;
//        is = isThereAnObjectOnTheWaySph(*r, sph);
//        if(!is) {
//          // arada cisim yoksa
//            cos = max(0,dotP(n,toUnit(r->dir)));
//            result = result + colMul(pointLightVector.at(i)->I, cos);
//        }
//    }
//    result = componentwiseMultiplication(material->dif, result);
//    return result;
//}

Color calculateSpecularAndDiffuseShadingTri(Triangle *tri, Material *material, Vec p, Vec v) {
    Color result(0,0,0);
    Color result2(0,0,0);
    Ray *r = new Ray();
    r->p = p;
    Vec n = tri->n;
    float cos;
    bool is;
    for(int i=0; i < pointLightVector.size(); i++) {
        r->dir = pointLightVector.at(i)->pos - p;
        is = isThereAnObjectOnTheWayTri(*r, tri);
        Vec l = toUnit(r->dir);
        if(!is) {
          // arada cisim yoksa
            
            //specular
            Vec h = toUnit(l+v);
            Color temp = pointLightVector.at(i)->I;
            float d = (pointLightVector.at(i)->pos - r->p).len();
            float att = 1 / (12.56 * d * d) ;
            temp.r *= att;
            temp.g *= att;
            temp.b *= att;
            
            
            result = result + colMul(temp, pow(max(0,dotP(n,h)), material->specExpP));

            //diff
            cos = max(0,dotP(n,toUnit(r->dir)));
            result2 = result2 + colMul(temp, cos);

        }
    }
    result = componentwiseMultiplication(material->spe, result);
    result2 = componentwiseMultiplication(material->dif, result2);
    return result + result2;

}

Color calculateSpecularAndDiffuseShadingSph(Sphere *sph, Material *material, Vec p, Vec v) {
    Color result(0,0,0);
    Color result2(0,0,0);
    Ray *r = new Ray();
    r->p = p;
    Vec n = toUnit(p - sph->center);
    Vec l,h;
    float cos;
    bool is;
    
    for(int i=0; i < pointLightVector.size(); i++) {
        r->dir = pointLightVector.at(i)->pos - p;
        is = isThereAnObjectOnTheWaySph(*r, sph);
        if(!is) {
            Color temp = pointLightVector.at(i)->I;
            float d = (pointLightVector.at(i)->pos - r->p).len();
            float att = 1 / (12.56 * d * d) ;
            temp.r *= att;
            temp.g *= att;
            temp.b *= att;
          // arada cisim yoksa
            //specular
            l= toUnit(r->dir);
            h = toUnit(l+v);
            result = result + colMul(temp, pow(max(0,dotP(h,n)), material->specExpP));

            //diffuse
            cos = max(0,dotP(n,toUnit(r->dir)));
            result2 = result2 + colMul(temp, cos);
        }

    }
    result = componentwiseMultiplication(material->spe, result);
    result2 = componentwiseMultiplication(material->dif, result2);
    return result + result2;


}


Color rayTrace(Ray *r, float len, int recursionDepth, bool isFromTri, int idd) {

    Color resColor(0,0,0);

    if(rayReflectCount < recursionDepth )
        return resColor;

    interReturn iR;
    if(recursionDepth == 0) {
        iR=findClosestInt(*r, len, false, false, -1);
    }
    else {
        iR=findClosestInt(*r, len, true, isFromTri, idd );
    }

    if(iR.doesinter==false) {
        if(recursionDepth == 0) {
            return backgroundColor;
        }
        else {
            return Color(0,0,0);
        }
    }



    if(iR.pointerClosest.sphp==NULL) {
            Material *mp = iR.pointerClosest.trip->materialP;
            resColor = resColor + componentwiseMultiplication(ambientLight,mp->amb);
            resColor = resColor + calculateSpecularAndDiffuseShadingTri(iR.pointerClosest.trip, mp, iR.intP, toUnit(tersCevir(r->dir)));
            // add color of the reflected ray
            Ray *refRay = new Ray();
            refRay->p = iR.intP;
            Vec vv = toUnit(r->dir);
            Vec nn = iR.pointerClosest.trip->n;
            refRay->dir = vv + vecMul(nn,2*dotP(nn, tersCevir(vv)));
            resColor = resColor + componentwiseMultiplication(mp->refl, rayTrace(refRay,0,recursionDepth+1,true,iR.pointerClosest.trip->id));

            //resColor = Color(0,255,0);
    }
    else {
            Material *mp = iR.pointerClosest.sphp->materialP;
            resColor = resColor + componentwiseMultiplication(ambientLight,mp->amb);
            resColor = resColor + calculateSpecularAndDiffuseShadingSph(iR.pointerClosest.sphp, mp, iR.intP, toUnit(tersCevir(r->dir)));
            // add color of the reflected ray
            Ray *refRay = new Ray();
            refRay->p = iR.intP;
            Vec vv = toUnit(r->dir);
            Vec nn = toUnit(iR.intP - iR.pointerClosest.sphp->center);
            refRay->dir = vv + vecMul(nn,2*dotP(nn, tersCevir(vv)));
            resColor = resColor + componentwiseMultiplication(mp->refl, rayTrace(refRay,0,recursionDepth+1, false, iR.pointerClosest.sphp->id));


            //resColor = Color(0,0,255);
    }


    return resColor;
}

void matchMaterialsToTriangles() {

    for(int i=0; i<triangleVector.size(); i++) {

        triangleVector.at(i)->materialP = materialMap[triangleVector.at(i)->matInd];

    }

    for(int i=0; i<sphereVector.size(); i++) {

        sphereVector.at(i)->materialP = materialMap[sphereVector.at(i)->matInd];

    }

}

void thread_f(int imin, int imax, int jmin, int jmax) {
    Ray *currentRay = new Ray();
    float lenTemp;
    for(int i=imin; i<imax; i++) {
        for(int j=jmin; j<jmax; j++) {
            currentRay->p = o;
            currentRay->dir = toUnit((firstPixel + vecMul(horizontalIncrement,j) - vecMul(verticalIncrement,i)) - o);

            lenTemp = currentRay->dir.len();
            imageArray[i][j] = rayTrace(currentRay, lenTemp, 0, false, -1);

        }
    }
}

int main(int argc, char** argv) {
    for (int i = 1; i<argc; i++) {
        clock_t startTime = clock();
    if(!readSceneFile(argv[i])) {
        cout << "file " << argv[i] << " could not be found, please type the file name correctly!\n";
        continue;
    }

    matchMaterialsToTriangles();


    ofstream outFile;


    Camera *currentCam;
    int currentHorRes, currentVerRes;
    int index = 0;
    for(vector<Camera*>::iterator it = cameras.begin(); it != cameras.end(); it++) {
        // Her camera dosyası icin

        currentCam = *it;
        currentHorRes = currentCam->HorRes;
        currentVerRes = currentCam->VerRes;
        outFile.open(currentCam->outputName.c_str(), std::fstream::out);
        outFile << "P3\n";
        outFile << currentHorRes << " " << currentVerRes << endl;
        outFile << "255\n";

        g = currentCam->gaze;
        g = toUnit(g);

        w = zeroVec - g;
        w = toUnit(w);

        v = currentCam ->up;
        v = toUnit(v);

        u = crossProduct(v,w);

        v = crossProduct(u,g);

        o = currentCam->pos;

        leftP = currentCam->left;
        rightP = currentCam->right;
        topP = currentCam->top;
        bottomP = currentCam->bottom;

        d = currentCam->distance;

        pixelW = abs((rightP-leftP)/currentHorRes);
        pixelH = abs((topP-bottomP)/currentVerRes);

        midd = o + vecMul(g,d) ;
        middLeft = midd + vecMul(u,leftP);
        topLeft = middLeft + vecMul(v,topP);
        firstPixel = topLeft + vecMul(u,(pixelW/2)) - vecMul(v,(pixelH/2));

        horizontalIncrement = vecMul(u,pixelW);
        verticalIncrement = vecMul(v,pixelH);



        imageArray = new Color*[5000];
        for(int i = 0; i < 5000; ++i)
            imageArray[i] = new Color[5000];

       thread_f (0, currentVerRes, 0, currentHorRes);


        Color tempColor;

        for(int i=0; i<currentVerRes; i++) {

            for(int j=0; j<currentHorRes; j++) {

                tempColor = imageArray[i][j];
                outFile << min(int(tempColor.r),255) << " " << min(int(tempColor.g),255) << " " << min(int(tempColor.b),255) << " ";

            }
            outFile << "\n";
        }

        for(int i=0; i<currentVerRes; i++) {
            delete [] imageArray[i];
        }
        delete [] imageArray;

        index++;
        outFile.close();
        clock_t endTime = clock();
        clock_t t = endTime - startTime;
        cout << argv [i] << ": " << (float)t/CLOCKS_PER_SEC <<" secs"<< "\n"  ;

    }
     deAllocate();
    }

   
   

    return 0;
}
