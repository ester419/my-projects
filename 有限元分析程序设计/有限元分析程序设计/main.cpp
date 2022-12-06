//
// Created by 张悦涵 on 2022/5/2.
//

#include <Eigen/Dense>//矩阵运算库
#include <iostream>
#include <fstream>
#include <string>
#include <cmath>
using namespace::Eigen;
using namespace::std;
// 节点类 包含2D 3D
// 且在我的设定中，节点类是通用的
class NodeBase
{
public:
    static int Ndslength;
    int ID{};
    double x{},y{},z{};
    int Dimen{};
    double DispX{},DispY{},DispZ{};
    double Fx = 0,Fy = 0,Fz = 0;// 三个方向的力
    double Cons_x = 0,Cons_y = 0,Cons_z = 0;//目前只有三个方向的约束
    void inputCoord3D(int id, double XX, double YY, double ZZ); // 成员函数 设置三点的坐标以及节点编号
    void inputCoord2D(int id, double XX, double YY);// 2D 两点坐标 以及节点编号
    void inputLoad3D(double F_x, double F_y, double F_z);// 3D 的三个方向的力 x y z
    void inputLoad2D(double F_x, double F_y);// 2D 两个方向的力 x y
    void inputConstraint3D(double Consx, double Consy, double Consz);//约束为0为自由，为1为约束 x y z
    void inputConstraint2D(double Consx, double Consy);// 施加约束 x y
    void dont_use_inputDisp2D(double Disp_X, double Disp_Y);// 无需在主函数中使用，此函数在System中调用
    void dont_use_inputDisp3D(double Disp_X, double Disp_Y, double Disp_Z);// 无需在主函数中使用，此函数在System中调用
};
int NodeBase::Ndslength = 0;//静态数据确定总数
//
void NodeBase::inputCoord3D(int id, double XX, double YY, double ZZ)
{
    Dimen = 3;//单元的维度
    x = XX;
    y = YY;
    z = ZZ;
    ID = id;
    Ndslength++;
}
void NodeBase::inputCoord2D(int id, double XX, double YY)
{
    Dimen = 2;
    x = XX;
    y = YY;
    ID = id;
    Ndslength++;
}
void NodeBase::inputLoad3D(double F_x, double F_y, double F_z)
{
    Fx = F_x;
    Fy = F_y;
    Fz = F_z;
}
void NodeBase::inputLoad2D(double F_x, double F_y)
{
    Fx = F_x;
    Fy = F_y;
}
void NodeBase::inputConstraint3D(double Consx, double Consy, double Consz)
{
    Cons_x = Consx;
    Cons_y = Consy;
    Cons_z = Consz;
}
void NodeBase::inputConstraint2D(double Consx, double Consy)
{
    Cons_x = Consx;
    Cons_y = Consy;

}
void NodeBase::dont_use_inputDisp2D(double Disp_X, double Disp_Y)//为System所调用的求解单元应力的函数
{
    DispX = Disp_X;
    DispY = Disp_Y;

}
void NodeBase::dont_use_inputDisp3D(double Disp_X, double Disp_Y, double Disp_Z)//为System所调用的求解单元应力的函数
{
    DispX = Disp_X;
    DispY = Disp_Y;
    DispZ = Disp_Z;
}



//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================



//单元的父类
//为了可以将不同的单元传入System进行求解
//所以将其归为一种父类
class Element
{
public:
    double E{},NU{},T{},A{},I{};//定义材料参数
    int ElsNodeNum{};//单元节点数量
    int ClassElsNum{};//同种单元的个数
    static int Dimen;

    NodeBase N1,N2,N3,N4,N5,N6,N7,N8;

    static int AllElsNum;//各种单元的总数

    MatrixXd B;//定义应变矩阵
    MatrixXd D;//定义本构矩阵
    MatrixXd K;//定义单元刚度矩阵
    Matrix<double, Dynamic, 1> ElsStress;//定义单元应力矩阵
    Matrix<double, Dynamic, 1> ElsDisp;//定义单元位移矩阵
    Matrix<double,4,1> VonMises;//三个主应力 一个蜂蜜塞斯应力
};
int Element::AllElsNum = 0;//初始值为0
int Element::Dimen = 0;







//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
/*
 *二维梁单元 自由度为3 分别是 x位移 y位移 以及转角
 * 但是目前无法实现与其余实体单元进行 刚度矩阵的组装
 */
class Beam2D2N : public Element
{
public:
    double L;//杆长
    static int Elsindex;//所代表的是这一类单元的总数
    void inputNode(NodeBase n1,NodeBase n2);// 传入的各个节点 以及单元坐标 计算应变矩阵B
    void inputENUT(double _E,double _I,double _A);// 计算弹性矩阵D和刚度矩阵K

private:
    void calculateT();//计算坐标变换矩阵
    void calculateB();//计算应变矩阵
    void calculateK();//计算刚度矩阵
    Matrix<double,6,6> T;
    Matrix<double,6,6> _B;
    Matrix<double,6,6> _K;


};
int Beam2D2N::Elsindex = 0;

void Beam2D2N::inputNode(NodeBase n1,NodeBase n2)
{
    N1 = n1;
    N2 = n2;
    Elsindex++;//每传入一个单元的参数，增加1
    Element::AllElsNum++;//Elm类的各种单元的总数添加
}
void Beam2D2N::inputENUT(double _E,double _I,double _A)// 计算弹性矩阵D和刚度矩阵K
{
    E = _E;
    I = _I;
    A = _A;
    calculateT();
    calculateK();
    K = T.transpose()*_K*T;//经过坐标变换后的刚度矩阵
    //K = _K;

}
void Beam2D2N::calculateT()//计算坐标变换矩阵
{
    L = sqrt((N1.x-N2.x)*(N1.x-N2.x)+(N1.y-N2.y)*(N1.y-N2.y));

    double lx = (N2.x-N1.x)/L;
    double mx = (N2.y-N1.y)/L;
    T<<     lx,  mx,  0,   0,   0,  0,
            -mx, lx,  0,   0,   0,  0,
            0,    0,  1,   0,   0,  0,
            0,    0,  0,  lx,  mx,  0,
            0,    0,  0,  -mx, lx,  0,
            0,    0,  0,   0 ,  0,  1;
}
void Beam2D2N::calculateK()
{
    double a00 =  E*A/L;
    double a03 = -a00;
    double a11 = 12*E*I/L*L*L;
    double a12 = 6*E*I/L*L;
    double a14 = -a11;
    double a22 = 4*E*I/L;
    double a24 = -a12;
    double a25 = 2*E*I/L;
    double a45 = -a12;

    _K<<    a00, 0.,  0.,  a03,  0., 0.,
            0., a11, a12,  0., a14,a12,
            0., a12, a22,  0., a24,a25,
            a03,  0.,  0., a00,  0., 0.,
            0., a14, a24,  0.,a11, a45,
            0., a12, a25,  0.,a45, a22;
}


//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================

class Tria2D3N : public Element
{
public:

    static int Elsindex;//所代表的是这一类单元的总数
    double A{};//单元面积
    void inputNode(NodeBase n1,NodeBase n2,NodeBase n3);// 传入的各个节点 以及单元坐标 计算应变矩阵B
    void inputENUT(double _E,double _NU,double _T);// 计算弹性矩阵D和刚度矩阵K

private:

    MatrixXd _B;//传回Element类的应变矩阵
    Matrix<double, 3, 3> _D;//穿回Elm类的本构矩阵
    //NodeBase N1,N2,N3;
    //注意顺序，逆时针读入，是局部单元的编号
    //计算B A

    void calculateB()
    {
        _B.resize(3,6);
        double beta1,beta2,beta3,gam1,gam2,gam3;

        A = (N1.x*(N2.y-N3.y)+N2.x*(N3.y-N1.y)+N3.x*(N1.y-N2.y))/2;// 面积

//        if (A<0)
//        {
//            A=-A;
//        }

        beta1 = N2.y-N3.y;
        beta2 = N3.y-N1.y;
        beta3 = N1.y-N2.y;
        gam1 = N3.x-N2.x;
        gam2 = N1.x-N3.x;
        gam3 = N2.x-N1.x;

        _B(0,0) = beta1;
        _B(0,1) = 0;
        _B(0,2) = beta2;
        _B(0,3) = 0;
        _B(0,4) = beta3;
        _B(0,5) = 0;

        _B(1,0) = 0;
        _B(1,1) = gam1;
        _B(1,2) = 0;
        _B(1,3) = gam2;
        _B(1,4) = 0;
        _B(1,5) = gam3;

        _B(2,0) = gam1;
        _B(2,1) = beta1;
        _B(2,2) = gam2;
        _B(2,3) = beta2;
        _B(2,4) = gam3;
        _B(2,5) = beta3;
        _B = _B/(2*A);
    };


    //计算弹性矩阵
    void calculateD()
    {
        double aa = E/(1-NU*NU);
        _D <<1,NU,0,NU,1,0,0,0,(1-NU)/2;
        _D = _D*aa;
    };

};


//私有结束
//=======================================================================================================================================================================
//共有开始

//此种单元的总个数
int Tria2D3N::Elsindex = 0;

void Tria2D3N::inputNode(NodeBase n1,NodeBase n2,NodeBase n3)
{
    N1 = n1;
    N2 = n2;
    N3 = n3;
    Elsindex++;//每传入一个单元的参数，增加1

    Element::AllElsNum++;//Elm类的各种单元的总数添加
}


//与四边形单元相同，都是在材料赋值的时候进行刚度矩阵的计算
//可在System中调用批量导入的方法，也可以单独导入材料参数


void Tria2D3N::inputENUT(double _E,double _NU,double _T)
{

    Element::Dimen = 2;//2D
    ClassElsNum = Elsindex;//将同种单元的个数赋值给Elm类 关键！
    NU = _NU;
    E = _E;
    T = _T;
    //位置不可用改
    calculateD();
    calculateB();
    B = _B;//Elm的B赋值完毕
    D = _D;
    K = A*T*B.transpose()*D*B;// 成功计算刚度矩阵
    ElsNodeNum = 3;//定义单元节点数

}






//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================







class Quad2D4N :public Element
{

public:
    //NodeBase N1,N2,N3,N4;
    void inputNode(NodeBase _N1,NodeBase _N2,NodeBase _N3,NodeBase _N4);// 传入节点
    void inputENUT(double _E,double _NU, double _T);
    static int Elsindex;
private:
    //为传入Elm的参数
    Matrix<double, 3, 8> __B;
    Matrix<double, 8, 8> __K;
    Matrix<double, 3, 3> _D;
    double J = 0;//雅克比 数

    Matrix<double, 3, 8> calculateB(const double *Guasspoint)
    {
        double s = Guasspoint[0];
        double t = Guasspoint[1];

        double x1 = N1.x;
        double x2 = N2.x;
        double x3 = N3.x;
        double x4 = N4.x;
        double y1 = N1.y;
        double y2 = N2.y;
        double y3 = N3.y;
        double y4 = N4.y;

        double a = 0.25*(y1*(s-1)+y2*(-1-s)+y3*(1+s)+y4*(1-s));
        double b = 0.25*(y1*(t-1)+y2*(1-t)+y3*(1+t)+y4*(-1-t));
        double c = 0.25*(x1*(t-1)+x2*(1-t)+x3*(1+t)+x4*(-1-t));
        double d = 0.25*(x1*(s-1)+x2*(-1-s)+x3*(1+s)+x4*(1-s));

        //为应变矩阵的每个元素
        double B100 = -0.25*a*(1-t)+0.25*b*(1-s);
        double B111 = -0.25*c*(1-s)+0.25*d*(1-t) ;
        double B120 = B111;
        double B121 = B100;

        double B200 = 0.25*a*(1-t)+0.25*b*(1+s);
        double B211 = -0.25*c*(1+s)-0.25*d*(1-t);
        double B220 = B211;
        double B221 = B200;

        double B300 = 0.25*a*(1+t)-0.25*b*(1+s);
        double B311 = 0.25*c*(1+s)-0.25*d*(1+t);
        double B320 = B311;
        double B321 = B300;

        double B400 = -0.25*a*(1+t)-0.25*b*(1-s);
        double B411 = 0.25*c*(1-s)+0.25*d*(1+t);
        double B420 = B411;
        double B421 = B400;

        //构造待求积分的应变矩阵
        Matrix<double, 3, 8> _B;

        _B<<B100,   0,B200,   0,B300,   0,B400,   0,

                0   ,B111,   0,B211,   0,B311,   0,B411,

                B120,B121,B220,B221,B320,B321,B420,B421;

        Matrix<double, 1, 4> X;
        Matrix<double, 4, 1> Y;

        X<<x1,x2,x3,x4;
        Y<<y1,y2,y3,y4;

        //定义雅克比矩阵
        Matrix<double, 4, 4> _J;
        _J<<0,1-t,t-s,s-1,t-1,0,s+1,-s-t,s-t,-s-1,0,t+1,1-s,s+t,-t-1,0;
        double JJ = X*_J*Y;
        JJ = JJ/8;
        _B = _B/JJ;//应变矩阵比上雅克比矩阵 此时应变矩阵的每一个元素均为待积分的量
        return _B;

    };//计算刚度矩阵 私有方法

    //高斯积分 计算子类的B和D
    //但是还需要再次传回父类
    void Guass()
    {

        Matrix<double, 8, 8> _K;//中介矩阵
        __K.setZero();
        __B.setZero();
        double GaussPoint[2]={0.577350269189626,-0.577350269189626};

        for (double & i : GaussPoint)
        {
            for (double & j : GaussPoint)
            {
                double GP[2] = {i,j};//定义必须在内部
                J+= calculateJ(GP);
                _K = calculateK(GP);
                __K = __K + _K;
            }
        }
        __K = T*__K;
    }

    //计算J 此时J是二元函数 需要高斯积分
    double calculateJ(const double *Guasspoint)
    {
        double s = Guasspoint[0];
        double t = Guasspoint[1];

        double x1 = N1.x;
        double x2 = N2.x;
        double x3 = N3.x;
        double x4 = N4.x;
        double y1 = N1.y;
        double y2 = N2.y;
        double y3 = N3.y;
        double y4 = N4.y;

        Matrix<double, 1, 4> X;
        Matrix<double, 4, 1> Y;

        X<<x1,x2,x3,x4;
        Y<<y1,y2,y3,y4;

        Matrix<double, 4, 4> _J;

        _J<<0,1-t,t-s,s-1,t-1,0,s+1,-s-t,s-t,-s-1,0,t+1,1-s,s+t,-t-1,0;

        double a = X*_J*Y;
        return a/8;

    };

    Matrix<double, 8, 8> calculateK(const double *Guasspoint)
    {
        Matrix<double, 3, 8> _B;
        Matrix<double, 1, 4> X;
        Matrix<double, 4, 1> Y;

        double s = Guasspoint[0];
        double t = Guasspoint[1];

        double x1 = N1.x;
        double x2 = N2.x;
        double x3 = N3.x;
        double x4 = N4.x;
        double y1 = N1.y;
        double y2 = N2.y;
        double y3 = N3.y;
        double y4 = N4.y;

        double a = 0.25*(y1*(s-1)+y2*(-1-s)+y3*(1+s)+y4*(1-s));
        double b = 0.25*(y1*(t-1)+y2*(1-t)+y3*(1+t)+y4*(-1-t));
        double c = 0.25*(x1*(t-1)+x2*(1-t)+x3*(1+t)+x4*(-1-t));
        double d = 0.25*(x1*(s-1)+x2*(-1-s)+x3*(1+s)+x4*(1-s));

        //为应变矩阵的每个元素

        double B100 = -0.25*a*(1-t)+0.25*b*(1-s);
        double B111 = -0.25*c*(1-s)+0.25*d*(1-t) ;
        double B120 = B111;
        double B121 = B100;

        double B200 = 0.25*a*(1-t)+0.25*b*(1+s);
        double B211 = -0.25*c*(1+s)-0.25*d*(1-t);
        double B220 = B211;
        double B221 = B200;

        double B300 = 0.25*a*(1+t)-0.25*b*(1+s);
        double B311 = 0.25*c*(1+s)-0.25*d*(1+t);
        double B320 = B311;
        double B321 = B300;

        double B400 = -0.25*a*(1+t)-0.25*b*(1-s);
        double B411 = 0.25*c*(1-s)+0.25*d*(1+t);
        double B420 = B411;
        double B421 = B400;

        //构造待求积分的应变矩阵

        _B<<B100,   0,B200,   0,B300,   0,B400,  0,
                0,   B111,0,   B211,0,   B311,0,  B411,
                B120,B121,B220,B221,B320,B321,B420,B421;

        X<<x1,x2,x3,x4;
        Y<<y1,y2,y3,y4;

        //定义雅克比矩阵
        Matrix<double, 4, 4> _J;
        _J<<0,1-t,t-s,s-1,t-1,0,s+1,-s-t,s-t,-s-1,0,t+1,1-s,s+t,-t-1,0;
        double JJ = X*_J*Y;
        JJ = JJ/8;
        Matrix<double, 3, 8> ___B;
        ___B = _B/JJ;//应变矩阵比上雅克比矩阵 此时应变矩阵的每一个元素均为待积分的量
        Matrix<double, 8, 8> _K;//刚度矩阵
        _K = JJ*___B.transpose()*_D*___B;
        return _K;
    };

    // 计算D本构矩阵
    void calcuateD()
    {
        double aa = E/(1-NU*NU);
        _D <<1,NU,0,NU,1,0,0,0,(1-NU)/2;
        _D = _D*aa;
    };

};

int Quad2D4N::Elsindex = 0;//单元总数 静态数据

void Quad2D4N::inputNode(NodeBase _N1,NodeBase _N2,NodeBase _N3,NodeBase _N4)
{
    N1 = _N1;
    N1 = _N1;
    N2 = _N2;
    N2 = _N2;
    N3 = _N3;
    N3 = _N3;
    N4 = _N4;
    N4 = _N4;
    Elsindex++;
    Element::AllElsNum++;//Elm类的各种单元的总数添加
}


void Quad2D4N::inputENUT(double _E, double _NU , double _T)
{

    Element::Dimen = 2;
    ClassElsNum = Elsindex;//将同种单元的个数赋值给Elm类 关键！
    E = _E;
    NU = _NU;
    T = _T;
    calcuateD();
    Guass();//积分
    ElsNodeNum = 4;
    K = __K;
    D = _D;
    double gp[] = {0,0};
    B = calculateB(gp);

}



// 2D四边形节点类定义结束




//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================


/*
 * 定义壳单元
 * Kirchhoff壳单元
 * 节点自由度数为3 分别是挠度 以及 两个方向的转角
 */

class Shell_Kirchhoff3D4N :public Element
{
public:
    void inputNode(NodeBase _N1,NodeBase _N2,NodeBase _N3,NodeBase _N4);
    void inputNode(double _E,double _NU,double _T);
    static int Elsindex;
private:
    double a,b;//a b分别为单元的长宽的一半

};
int Shell_Kirchhoff3D4N::Elsindex = 0;

void Shell_Kirchhoff3D4N::inputNode(NodeBase _N1,NodeBase _N2,NodeBase _N3,NodeBase _N4)
{
    N1 = _N1;
    N2 = _N2;
    N3 = _N3;
    N4 = _N4;
    Elsindex++;
    Element::AllElsNum++;//Elm类的各种单元的总数添加



}


//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================

class Tetra3D4N : public Element
{
public:
    void inputNode(NodeBase _N1,NodeBase _N2,NodeBase _N3,NodeBase _N4);// 传入节点
    void inputENUT(double _E,double _NU);//3D单元不需要厚度
    static int Elsindex;

private:
    double V{};
    Matrix<double,6,12> B612;
    Matrix<double,6,6> D66;
    Matrix<double,12,12> K1212;

    void calculateD();
    void calculateV();
    void calculateB();
    void calculateK();

};

int Tetra3D4N::Elsindex = 0;

void Tetra3D4N::inputNode(NodeBase _N1, NodeBase _N2, NodeBase _N3, NodeBase _N4)
{
    N1 = _N1;
    N1 = _N1;
    N2 = _N2;
    N2 = _N2;
    N3 = _N3;
    N3 = _N3;
    N4 = _N4;
    N4 = _N4;
    Elsindex++;
    Element::AllElsNum++;//Elm类的各种单元的总数添加
}

void Tetra3D4N::inputENUT(double _E, double _NU)
{

    ClassElsNum = Elsindex;
    ElsNodeNum = 4;
    Element::Dimen = 3;//维度
    E = _E;
    NU = _NU;
    calculateD();
    calculateV();
    calculateB();
    calculateK();
    B = B612;
    D = D66;
    K = K1212;

}
//计算本构矩阵
void Tetra3D4N::calculateD()
{
    double aa = E/((1+NU)*(1-2*NU));
    double b = 1-NU;
    double c = (1-2*NU)/2;
    D66 <<  b,NU,NU,0,0,0,
            NU,b,NU,0,0,0,
            NU,NU,b,0,0,0,
            0, 0,0,c,0,0,
            0, 0,0,0,c,0,
            0, 0,0,0,0,c;
    D66 = aa*D66;
}
//计算体积
void Tetra3D4N::calculateV()
{
    Matrix<double,4,4> xyz;
    double x1 = N1.x;
    double x2 = N2.x;
    double x3 = N3.x;
    double x4 = N4.x;

    double y1 = N1.y;
    double y2 = N2.y;
    double y3 = N3.y;
    double y4 = N4.y;

    double z1 = N1.z;
    double z2 = N2.z;
    double z3 = N3.z;
    double z4 = N4.z;
    xyz <<  1,x1,y1,z1,
            1,x2,y2,z2,
            1,x3,y3,z3,
            1,x4,y4,z4;
    V = xyz.determinant()/6;

}

void Tetra3D4N::calculateB()
{
    double x1 = N1.x;
    double x2 = N2.x;
    double x3 = N3.x;
    double x4 = N4.x;

    double y1 = N1.y;
    double y2 = N2.y;
    double y3 = N3.y;
    double y4 = N4.y;

    double z1 = N1.z;
    double z2 = N2.z;
    double z3 = N3.z;
    double z4 = N4.z;

    Matrix<double,3,3> mbeta1;
    Matrix<double,3,3> mbeta2;
    Matrix<double,3,3> mbeta3;
    Matrix<double,3,3> mbeta4;

    Matrix<double,3,3> mgamma1;
    Matrix<double,3,3> mgamma2;
    Matrix<double,3,3> mgamma3;
    Matrix<double,3,3> mgamma4;

    Matrix<double,3,3> mdelta1;
    Matrix<double,3,3> mdelta2;
    Matrix<double,3,3> mdelta3;
    Matrix<double,3,3> mdelta4;

    mbeta1 << 1,y2,z2,
            1,y3,z3,
            1,y4,z4;

    mbeta2 << 1,y1,z1,
            1,y3,z3,
            1,y4,z4;

    mbeta3 << 1,y1,z1,
            1,y2,z2,
            1,y4,z4;

    mbeta4 << 1,y1,z1,
            1,y2,z2,
            1,y3,z3;

    mgamma1 << 1,x2,z2,
            1,x3,z3,
            1,x4,z4;

    mgamma2 << 1,x1,z1,
            1,x3,z3,
            1,x4,z4;

    mgamma3 << 1,x1,z1,
            1,x2,z2,
            1,x4,z4;

    mgamma4 << 1,x1,z1,
            1,x2,z2,
            1,x3,z3;

    mdelta1 << 1,x2,y2,
            1,x3,y3,
            1,x4,y4;

    mdelta2 << 1,x1,y1,
            1,x3,y3,
            1,x4,y4;

    mdelta3 << 1,x1,y1,
            1,x2,y2,
            1,x4,y4;

    mdelta4 << 1,x1,y1,
            1,x2,y2,
            1,x3,y3;

    double beta1 = -1*mbeta1.determinant();
    double beta2 = mbeta2.determinant();
    double beta3 = -1*mbeta3.determinant();
    double beta4 = mbeta4.determinant();

    double gamma1 = mgamma1.determinant();
    double gamma2 = -1*mgamma2.determinant();
    double gamma3 = mgamma3.determinant();
    double gamma4 = -1*mgamma4.determinant();

    double delta1 = -1*mdelta1.determinant();
    double delta2 = mdelta2.determinant();
    double delta3 = -1*mdelta3.determinant();
    double delta4 = mdelta4.determinant();

    B612.setZero();
    B612 << beta1 , 0      ,     0 , beta2 ,      0 ,     0 , beta3 , 0      ,     0 , beta4 , 0      , 0     ,
            0     , gamma1 ,     0 , 0     , gamma2 ,     0 , 0     , gamma3 ,     0 , 0     , gamma4 ,     0 ,
            0     , 0      ,delta1 , 0     , 0      ,delta2 , 0     , 0      ,delta3 , 0     , 0      ,delta4 ,
            gamma1, beta1  ,0      , gamma2, beta2  ,0      , gamma3, beta3  ,0      , gamma4, beta4  ,0      ,
            0     , delta1 ,gamma1 , 0     , delta2 ,gamma2 , 0     , delta3 ,gamma3 ,  0     , delta4 ,gamma4,
            delta1, 0      ,beta1  , delta2, 0      ,beta2  , delta3, 0      ,beta3  , delta4, 0      ,beta4  ;
    B612 = B612/(6*V);
}

void Tetra3D4N::calculateK()
{
    if (V<0)
    {
        V = -1*V;
    }
    K1212 = V*B612.transpose()*D66*B612;
}




//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//求解类





class System
{
public:
    ofstream Outfile;
    MatrixXd KK,CKK;//kk是总刚度矩阵，ckk是加载完约束的刚度矩阵。
    Matrix<double, Dynamic, 1> FF;//载荷向量
    Matrix<double, Dynamic, 1> UU;//位移向量
    Matrix<double, Dynamic, 1> RF;//支反力向量


    void inputels(Element _els[],NodeBase _nds[]);//计算 并 求解！
    //void inputEUNT(Element _els[],double _E,double _NU,double _T);// 批量输入材料参数，对全部单元均生效，若有不同单元则单独为单元赋材料参数

private:
    int lengthels{};
    int lengthnds{};
    int Dimen{};
    void calculateK(Element _els[]);
    void calculateConstraint(NodeBase _nds[]);
    void calculateFF(NodeBase _nds[]);
    void calculateUU();
    void calculateElsStress(Element _els[],NodeBase _nds[]);
    void calculateVonMises(Element _els[]) const;
    void Printall(Element _els[]);
    static string getTime()//获得时间的函数
    {
        time_t timep;
        time (&timep); //获取time_t类型的当前时间
        char tmp[64];
        strftime(tmp, sizeof(tmp), "%Y-%m-%d %H:%M:%S",localtime(&timep) );//对日期和时间进行格式化
        return tmp;
    };
    static void sort(double a[],int size)//数组从大到小排列函数 用于 第一 第二 第三主应力的排列
    {
        for (int j=0;j<size;j++)
        {
            double max=a[j];

            int mink=j;

            for (int k=j;k<size;k++)
            {
                if (a[k] > max)
                {
                    max=a[k];
                    mink=k;
                }
            }
            double temp=a[j];
            a[j]=a[mink];
            a[mink]=temp;
        }
    };

};
void System::inputels(Element _els[],NodeBase _nds[]) {
    lengthels = Element::AllElsNum;//全部单元的个数
    lengthnds = NodeBase::Ndslength;//是节点的总数
    Dimen = _nds[0].Dimen;
    calculateK(_els);
    calculateConstraint(_nds);
    calculateFF(_nds);
    calculateUU();
    calculateElsStress(_els,_nds);
    calculateVonMises(_els);
    Printall(_els);

}

// 自动打印
void System::Printall(Element _els[])
{

    Outfile.open("/Users/dailinzhang/CLionProjects/FEM/Solve.txt");
//    Outfile<<"\n总刚度矩阵          ：\n"<<KK<<"\n"<<endl;
//    Outfile<<"\n加载约束后的总刚度矩阵：\n"<<CKK<<"\n"<<endl;
//    Outfile<<"\n节点载荷向量        ：\n"<<FF<<"\n"<<endl;
//    Outfile<<"\n节点位移向量        ：\n"<<UU<<"\n"<<endl;

    string   time = getTime();
    Outfile<<"********************************************************************************************"<<endl;
    Outfile<<"********"<<"                           Name:     "<<"E--FEM"<<"                                 ********"<<endl;
    Outfile<<"********"<<"                           Time:     "<<time<<"                    "<<"********"<<endl;
    Outfile<<"********"<<"                           Author:   "<<"Dailin Zhang"<<"                           ********"<<endl;
    Outfile<<"********************************************************************************************"<<endl;
    Outfile<<"\n\n"<<"单元维度：        "<<Element::Dimen<<endl;
    Outfile<<"单元总数：        "<<Element::AllElsNum<<endl;



    if (Element::Dimen == 3)
    {
        if (Tetra3D4N::Elsindex != 0)
        {
            Outfile<<"四面体单元总数：   "<<Tetra3D4N::Elsindex<<endl;
        }
        if (Hexah3D8N::Elsindex != 0)
        {
            Outfile<<"六面体单元总数：   "<<Hexah3D8N::Elsindex<<endl;
        }

        Outfile<<"节点总数：        "<<NodeBase::Ndslength<<endl;


        Outfile<<"\n"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"********"<<"                                  "<<"节点位移"<<"                                    ********"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"\n"<<endl;


        for (int i = 0; i < lengthnds; ++i)
        {
            Outfile<<"节点编号："<<setw(4)<<i+1
                   <<"    DispX = "<<setw(13)<<UU(3*i,0)
                   <<"    DispY = "<<setw(13)<<UU(3*i+1,0)
                   <<"    DispZ = "<<setw(13)<<UU(3*i+2,0)
                   <<"    和位移 = "<<setw(13)<<sqrt(UU(3*i,0)*UU(3*i,0)+UU(3*i+1,0)*UU(3*i+1,0)+UU(3*i+2,0)*UU(3*i+2,0))

                   <<endl;
        }

        Outfile<<"\n"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"********"<<"                                "<<"单元应力"<<"                                      ********"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"\n"<<endl;


        for (int i = 0; i < lengthels; ++i)
        {
            Outfile<<"单元编号："<<setw(4)<<i+1
                   <<"    x正应力："<<setw(13)<<_els[i].ElsStress(0,0)
                   <<"    y正应力："<<setw(13)<<_els[i].ElsStress(1,0)
                   <<"    z正应力："<<setw(13)<<_els[i].ElsStress(2,0)
                   <<"    xy剪应力："<<setw(13)<<_els[i].ElsStress(3,0)
                   <<"    yz剪应力："<<setw(13)<<_els[i].ElsStress(4,0)
                   <<"    zx剪应力："<<setw(13)<<_els[i].ElsStress(5,0)
                   <<"    第一主应力："<<setw(13)<<_els[i].VonMises(0,0)
                   <<"    第二主应力："<<setw(13)<<_els[i].VonMises(1,0)
                   <<"    第三主应力："<<setw(13)<<_els[i].VonMises(2,0)
                   <<"    VonMises应力："<<setw(13)<<_els[i].VonMises(3,0)<<endl;
        }

        Outfile<<"\n"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"********"<<"                                   "<<"支反力"<<"                                     ********"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"\n"<<endl;

        for (int i = 0; i < lengthnds; ++i)
        {
            Outfile << "节点编号：" << setw(4) << i + 1
                    << "    支反力X = " << setw(13) << RF(3 * i, 0)
                    << "    支反力Y = " << setw(13) << RF(3 * i + 1, 0)
                    << "    支反力Z = " << setw(13) << RF(3 * i + 2, 0) << endl;
        }
    }





    if (Dimen == 2)
    {
        if (Tria2D3N::Elsindex != 0)
        {
            Outfile<<"三角形单元总数：   "<<Tria2D3N::Elsindex<<endl;
        }if (Quad2D4N::Elsindex != 0)
        {
            Outfile<<"四边形单元总数：   "<<Quad2D4N::Elsindex<<endl;
        }
        Outfile<<"节点总数：        "<<NodeBase::Ndslength<<endl;

        Outfile<<"\n"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"********"<<"                                  "<<"节点位移"<<"                                    ********"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"\n"<<endl;


        for (int i = 0; i < lengthnds; ++i)
        {
            Outfile<<"节点编号："<<setw(4)<<i+1
                   <<"    DispX = "<<setw(13)<<UU(2*i,0)
                   <<"    DispY = "<<setw(13)<<UU(2*i+1,0)
                   <<"    和位移 = "<<setw(13)<<sqrt(UU(2*i,0)*UU(2*i,0)+UU(2*i+1,0)*UU(2*i+1,0))

                   <<endl;
        }


        Outfile<<"\n"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"********"<<"                                  "<<"单元应力"<<"                                    ********"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"\n"<<endl;


        for (int i = 0; i < lengthels; ++i)
        {
            Outfile<<"单元："<<setw(4)<<i+1
                   <<"    x正应力："<<setw(13)<<_els[i].ElsStress(0,0)
                   <<"    y正应力："<<setw(13)<<_els[i].ElsStress(1,0)
                   <<"    xy剪应力："<<setw(13)<<_els[i].ElsStress(2,0)
                   <<"    第一主应力："<<setw(13)<<_els[i].VonMises(0,0)
                   <<"    第二主应力："<<setw(13)<<_els[i].VonMises(1,0)
                   <<"    第三主应力："<<setw(13)<<_els[i].VonMises(2,0)
                   <<"    VonMises应力："<<setw(13)<<_els[i].VonMises(3,0)<<endl;
        }


        Outfile<<"\n"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"********"<<"                                   "<<"支反力"<<"                                     ********"<<endl;
        Outfile<<"*********************************************************************************************"<<endl;
        Outfile<<"\n"<<endl;


        for (int i = 0; i < lengthnds; ++i)
        {
            Outfile<<"节点编号："<<setw(4)<<i+1
                   <<"    支反力X = "<<setw(13)<<RF(2*i,0)
                   <<"    支反力Y = "<<setw(13)<<RF(2*i+1,0)<<endl;
        }

    }
    Outfile.close();
}


void System::calculateK(Element *_els)
{
    int DOF1[9];
    int DOF2[7];
    int DOF3[13];
    int DOF4[25];

    Matrix<double, Dynamic, Dynamic> MidKK;

    if (Dimen == 2)
    {
        MidKK.resize(2*lengthnds,2*lengthnds);
        MidKK.setZero();
        for (int q = 0; q<lengthels; q++)
        {
            //四边形单元的刚度矩阵的组装
            if (_els[q].ElsNodeNum == 4)
            {

                //第一批 1 2 3 4
                //第二批 3 4 5 6
                DOF1[0] = 0;
                DOF1[1] = 2*_els[q].N1.ID-1;// 1 5
                DOF1[2] = 2*_els[q].N1.ID;  // 2 6
                DOF1[3] = 2*_els[q].N2.ID-1;// 3 7
                DOF1[4] = 2*_els[q].N2.ID;  // 4 8
                DOF1[5] = 2*_els[q].N3.ID-1;// 5 9
                DOF1[6] = 2*_els[q].N3.ID;  // 6 10
                DOF1[7] = 2*_els[q].N4.ID-1;// 7 11
                DOF1[8] = 2*_els[q].N4.ID;  // 8 12

                for (int i = 1; i<=8; i++)
                {
                    for (int j = 1; j<=8; j++)
                    {
                        MidKK(DOF1[i]-1,DOF1[j]-1) = MidKK(DOF1[i]-1,DOF1[j]-1)+_els[q].K(i-1,j-1);
                    }
                }
            }

            //三角形单元刚度矩阵的组装
            if (_els[q].ElsNodeNum == 3)
            {
                DOF2[0] = 0;
                DOF2[1] = 2*_els[q].N1.ID-1;// 3 5
                DOF2[2] = 2*_els[q].N1.ID;  // 4 6
                DOF2[3] = 2*_els[q].N2.ID-1;// 5 3
                DOF2[4] = 2*_els[q].N2.ID;  // 6 4
                DOF2[5] = 2*_els[q].N3.ID-1;// 7 2
                DOF2[6] = 2*_els[q].N3.ID;  // 8 1

                for (int i=1; i<=6; i++)
                {
                    for (int j=1; j<=6; j++)
                    {
                        MidKK(DOF2[i]-1,DOF2[j]-1) = MidKK(DOF2[i]-1,DOF2[j]-1)+_els[q].K(i-1,j-1);
                    }
                }
            }
        }
        KK = MidKK;
    }
    if (Dimen == 3)
    {
        MidKK.resize(3*lengthnds,3*lengthnds);
        MidKK.setZero();

        for (int q = 0; q < lengthels; q++) {
            if (_els[q].ElsNodeNum == 4)
            {
                DOF3[0] = 0;
                //1 2 3 4
                DOF3[1] = 3*_els[q].N1.ID-2;// 1
                DOF3[2] = 3*_els[q].N1.ID-1;// 2
                DOF3[3] = 3*_els[q].N1.ID;  // 3

                DOF3[4] = 3*_els[q].N2.ID-2;// 4
                DOF3[5] = 3*_els[q].N2.ID-1;// 5
                DOF3[6] = 3*_els[q].N2.ID;  // 6

                DOF3[7] = 3*_els[q].N3.ID-2;
                DOF3[8] = 3*_els[q].N3.ID-1;
                DOF3[9] = 3*_els[q].N3.ID;

                DOF3[10] = 3*_els[q].N4.ID-2;
                DOF3[11] = 3*_els[q].N4.ID-1;
                DOF3[12] = 3*_els[q].N4.ID;

                for (int i = 1; i <= 12; i++)
                {
                    for (int j = 1; j <= 12; j++)
                    {
                        MidKK(DOF3[i]-1,DOF3[j]-1) = MidKK(DOF3[i]-1,DOF3[j]-1)+_els[q].K(i-1,j-1);
                    }
                }
            }

            if (_els[q].ElsNodeNum == 8)
            {
                DOF4[0] = 0;
                //1 2 3 4 5 6 7 8
                DOF4[1] = 3*_els[q].N1.ID-2;// 1
                DOF4[2] = 3*_els[q].N1.ID-1;// 2
                DOF4[3] = 3*_els[q].N1.ID;  // 3

                DOF4[4] = 3*_els[q].N2.ID-2;// 4
                DOF4[5] = 3*_els[q].N2.ID-1;// 5
                DOF4[6] = 3*_els[q].N2.ID;  // 6

                DOF4[7] = 3*_els[q].N3.ID-2;// 7
                DOF4[8] = 3*_els[q].N3.ID-1;// 8
                DOF4[9] = 3*_els[q].N3.ID;  // 9

                DOF4[10] = 3*_els[q].N4.ID-2;
                DOF4[11] = 3*_els[q].N4.ID-1;
                DOF4[12] = 3*_els[q].N4.ID;

                DOF4[13] = 3*_els[q].N5.ID-2;
                DOF4[14] = 3*_els[q].N5.ID-1;
                DOF4[15] = 3*_els[q].N5.ID;

                DOF4[16] = 3*_els[q].N6.ID-2;
                DOF4[17] = 3*_els[q].N6.ID-1;
                DOF4[18] = 3*_els[q].N6.ID;

                DOF4[19] = 3*_els[q].N7.ID-2;
                DOF4[20] = 3*_els[q].N7.ID-1;
                DOF4[21] = 3*_els[q].N7.ID;

                DOF4[22] = 3*_els[q].N8.ID-2;
                DOF4[23] = 3*_els[q].N8.ID-1;
                DOF4[24] = 3*_els[q].N8.ID;

                for (int i = 1; i <= 24; i++)
                {
                    for (int j = 1; j <= 24; j++)
                    {
                        MidKK(DOF4[i]-1,DOF4[j]-1) = MidKK(DOF4[i]-1,DOF4[j]-1)+_els[q].K(i-1,j-1);
                    }
                }
            }
        }
        KK = MidKK;
    }

}

void System::calculateConstraint(NodeBase *_nds)//处理总刚度矩阵 依照维度为主
{

    if (Dimen == 2)
    {
        MatrixXd OneKK;
        OneKK = MatrixXd::Identity(2 * lengthnds, 2 * lengthnds);//定义一个全一阵
        MatrixXd transKK = KK;
        //加载约束
        //值得注意的是！ x-2 y-1！
        for (int i = 0; i < lengthnds; ++i)
        {
            if (_nds[i].Cons_x == 1)
            {
                transKK.row(2 * _nds[i].ID - 2) = OneKK.row(2 * _nds[i].ID - 2);
                transKK.col(2 * _nds[i].ID - 2) = OneKK.col(2 * _nds[i].ID - 2);

            }

            if (_nds[i].Cons_y == 1)
            {
                transKK.row(2 * _nds[i].ID - 1) = OneKK.row(2 * _nds[i].ID - 1);
                transKK.col(2 * _nds[i].ID - 1) = OneKK.col(2 * _nds[i].ID - 1);
            }
        }
        CKK = transKK;
    }

    if (Dimen == 3)
    {
        MatrixXd OneKK;
        OneKK = MatrixXd::Identity(3 * lengthnds, 3 * lengthnds);//定义一个全一阵
        MatrixXd transKK = KK;
        for (int i = 0; i < lengthnds; ++i)
        {
            if (_nds[i].Cons_x == 1)
            {
                transKK.row(3 * _nds[i].ID - 3) = OneKK.row(3 * _nds[i].ID - 3);
                transKK.col(3 * _nds[i].ID - 3) = OneKK.col(3 * _nds[i].ID - 3);
            }

            if (_nds[i].Cons_y == 1)
            {
                transKK.row(3 * _nds[i].ID - 2) = OneKK.row(3 * _nds[i].ID - 2);
                transKK.col(3 * _nds[i].ID - 2) = OneKK.col(3 * _nds[i].ID - 2);
            }

            if (_nds[i].Cons_z == 1)
            {
                transKK.row(3 * _nds[i].ID - 1) = OneKK.row(3 * _nds[i].ID - 1);
                transKK.col(3 * _nds[i].ID - 1) = OneKK.col(3 * _nds[i].ID - 1);
            }

        }
        CKK = transKK;
    }
}

void System::calculateFF(NodeBase *_nds)//通过对节点外载荷进行排序 获得外载荷列阵
{
    if (Dimen == 2)
    {
        FF.resize(2 * lengthnds, 1);
        FF.setZero();
        for (int i = 0; i < lengthnds; i++)
        {
            FF(2 * i, 0) = _nds[i].Fx;
            FF(2 * i + 1, 0) = _nds[i].Fy;
        }
    }
    if (Dimen == 3)
    {
        FF.resize(3 * lengthnds, 1);
        FF.setZero();
        for (int i = 0; i < lengthnds; i++)
        {
            FF(3 * i, 0) = _nds[i].Fx;
            FF(3 * i + 1, 0) = _nds[i].Fy;
            FF(3 * i + 2, 0) = _nds[i].Fz;
        }
    }

}


void System::calculateUU()
{
    //位移向量
//    UU = CKK.colPivHouseholderQr().solve(FF);
    UU = CKK.inverse()*FF;
    RF = KK*UU-FF;
    //修正一些微小位移和支反力
    if (Dimen == 2)
    {
        for (int i = 0; i < 2*lengthnds; i++)
        {
            if((UU(i,0)<1e-8)&&(UU(i,0)>-1e-8))
            {
                UU(i,0) = 0;
            }
            if((RF(i,0)<1e-8)&&(RF(i,0)>-1e-8))
            {
                RF(i,0) = 0;
            }
        }
    }
    else if (Dimen == 3)
    {
        for (int i = 0; i < 3*lengthnds; i++)
        {
            if((UU(i,0)<1e-8)&&(UU(i,0)>-1e-8))
            {
                UU(i,0) = 0;
            }
            if((RF(i,0)<1e-8)&&(RF(i,0)>-1e-8))
            {
                RF(i,0) = 0;
            }
        }
    }
}


void System::calculateElsStress(Element _els[], NodeBase _nds[])
{
    if (Dimen == 2)
    {
        for (int i = 0; i < lengthnds; ++i)
        {
            _nds[i].dont_use_inputDisp2D(UU(2*i,0),UU(2*i+1,0));
        }
        for (int i = 0; i < lengthels; i++)
        {
            if (_els[i].ElsNodeNum == 4)
            {
                // 传输位移数据回单元的各个节点
                _els[i].N1.dont_use_inputDisp2D(UU(2 * (_els[i].N1.ID - 1), 0), UU(2 * (_els[i].N1.ID - 1) + 1, 0));
                _els[i].N2.dont_use_inputDisp2D(UU(2 * (_els[i].N2.ID - 1), 0), UU(2 * (_els[i].N2.ID - 1) + 1, 0));
                _els[i].N3.dont_use_inputDisp2D(UU(2 * (_els[i].N3.ID - 1), 0), UU(2 * (_els[i].N3.ID - 1) + 1, 0));
                _els[i].N4.dont_use_inputDisp2D(UU(2 * (_els[i].N4.ID - 1), 0), UU(2 * (_els[i].N4.ID - 1) + 1, 0));
            }
            if (_els[i].ElsNodeNum == 3)
            {

                _els[i].N1.dont_use_inputDisp2D(UU(2 * (_els[i].N1.ID - 1), 0), UU(2 * (_els[i].N1.ID - 1) + 1, 0));
                _els[i].N2.dont_use_inputDisp2D(UU(2 * (_els[i].N2.ID - 1), 0), UU(2 * (_els[i].N2.ID - 1) + 1, 0));
                _els[i].N3.dont_use_inputDisp2D(UU(2 * (_els[i].N3.ID - 1), 0), UU(2 * (_els[i].N3.ID - 1) + 1, 0));
            }
        }
        Matrix<double, 8, 1> TelsUU;
        Matrix<double, 6, 1> QelsUU;
        //计算单元应力
        for (int i = 0; i < lengthels; i++)
        {
            if (_els[i].ElsNodeNum == 4)
            {

                TelsUU << _els[i].N1.DispX, _els[i].N1.DispY,
                        _els[i].N2.DispX, _els[i].N2.DispY,
                        _els[i].N3.DispX, _els[i].N3.DispY,
                        _els[i].N4.DispX, _els[i].N4.DispY;

                _els[i].ElsDisp = TelsUU;
                _els[i].ElsStress = _els[i].D * _els[i].B * _els[i].ElsDisp;

            }

            if (_els[i].ElsNodeNum == 3)
            {
                QelsUU << _els[i].N1.DispX, _els[i].N1.DispY,
                        _els[i].N2.DispX, _els[i].N2.DispY,
                        _els[i].N3.DispX, _els[i].N3.DispY;
                _els[i].ElsDisp = QelsUU;
                _els[i].ElsStress = _els[i].D * _els[i].B * _els[i].ElsDisp;
            }
        }
    }
    if (Dimen == 3)
    {
        for (int i = 0; i < lengthnds; ++i)
        {
            _nds[i].dont_use_inputDisp3D(UU(3*i,0),UU(3*i+1,0),UU(3*+2,0));
        }
        for (int i = 0; i < lengthels; ++i)
        {
            if (_els[i].ElsNodeNum == 4)
            {
                _els[i].N1.dont_use_inputDisp3D(UU(3 * (_els[i].N1.ID - 1), 0), UU(3 * (_els[i].N1.ID - 1) + 1, 0),UU(3 * (_els[i].N1.ID - 1) + 2, 0));
                _els[i].N2.dont_use_inputDisp3D(UU(3 * (_els[i].N2.ID - 1), 0), UU(3 * (_els[i].N2.ID - 1) + 1, 0),UU(3 * (_els[i].N2.ID - 1) + 2, 0));
                _els[i].N3.dont_use_inputDisp3D(UU(3 * (_els[i].N3.ID - 1), 0), UU(3 * (_els[i].N3.ID - 1) + 1, 0),UU(3 * (_els[i].N3.ID - 1) + 2, 0));
                _els[i].N4.dont_use_inputDisp3D(UU(3 * (_els[i].N4.ID - 1), 0), UU(3 * (_els[i].N4.ID - 1) + 1, 0),UU(3 * (_els[i].N4.ID - 1) + 2, 0));
            }
            if (_els[i].ElsNodeNum == 8)
            {
                _els[i].N1.dont_use_inputDisp3D(UU(3 * (_els[i].N1.ID - 1), 0), UU(3 * (_els[i].N1.ID - 1) + 1, 0),UU(3 * (_els[i].N1.ID - 1) + 2, 0));
                _els[i].N2.dont_use_inputDisp3D(UU(3 * (_els[i].N2.ID - 1), 0), UU(3 * (_els[i].N2.ID - 1) + 1, 0),UU(3 * (_els[i].N2.ID - 1) + 2, 0));
                _els[i].N3.dont_use_inputDisp3D(UU(3 * (_els[i].N3.ID - 1), 0), UU(3 * (_els[i].N3.ID - 1) + 1, 0),UU(3 * (_els[i].N3.ID - 1) + 2, 0));
                _els[i].N4.dont_use_inputDisp3D(UU(3 * (_els[i].N4.ID - 1), 0), UU(3 * (_els[i].N4.ID - 1) + 1, 0),UU(3 * (_els[i].N4.ID - 1) + 2, 0));
                _els[i].N5.dont_use_inputDisp3D(UU(3 * (_els[i].N5.ID - 1), 0), UU(3 * (_els[i].N5.ID - 1) + 1, 0),UU(3 * (_els[i].N5.ID - 1) + 2, 0));
                _els[i].N6.dont_use_inputDisp3D(UU(3 * (_els[i].N6.ID - 1), 0), UU(3 * (_els[i].N6.ID - 1) + 1, 0),UU(3 * (_els[i].N6.ID - 1) + 2, 0));
                _els[i].N7.dont_use_inputDisp3D(UU(3 * (_els[i].N7.ID - 1), 0), UU(3 * (_els[i].N7.ID - 1) + 1, 0),UU(3 * (_els[i].N7.ID - 1) + 2, 0));
                _els[i].N8.dont_use_inputDisp3D(UU(3 * (_els[i].N8.ID - 1), 0), UU(3 * (_els[i].N8.ID - 1) + 1, 0),UU(3 * (_els[i].N8.ID - 1) + 2, 0));
            }
        }
        Matrix<double, 12, 1> TelsUU;
        Matrix<double, 24, 1> HelsUU;
        for (int i = 0; i < lengthels; ++i)
        {

            if (_els[i].ElsNodeNum == 4)
            {
                TelsUU <<  _els[i].N1.DispX, _els[i].N1.DispY, _els[i].N1.DispZ,
                        _els[i].N2.DispX, _els[i].N2.DispY, _els[i].N2.DispZ,
                        _els[i].N3.DispX, _els[i].N3.DispY, _els[i].N3.DispZ,
                        _els[i].N4.DispX, _els[i].N4.DispY, _els[i].N4.DispZ;

                _els[i].ElsDisp = TelsUU;
                _els[i].ElsStress = _els[i].D * _els[i].B * _els[i].ElsDisp;
            }
            if (_els[i].ElsNodeNum == 8)
            {
                HelsUU << _els[i].N1.DispX, _els[i].N1.DispY, _els[i].N1.DispZ,
                        _els[i].N2.DispX, _els[i].N2.DispY, _els[i].N2.DispZ,
                        _els[i].N3.DispX, _els[i].N3.DispY, _els[i].N3.DispZ,
                        _els[i].N4.DispX, _els[i].N4.DispY, _els[i].N4.DispZ,
                        _els[i].N5.DispX, _els[i].N5.DispY, _els[i].N5.DispZ,
                        _els[i].N6.DispX, _els[i].N6.DispY, _els[i].N6.DispZ,
                        _els[i].N7.DispX, _els[i].N7.DispY, _els[i].N7.DispZ,
                        _els[i].N8.DispX, _els[i].N8.DispY, _els[i].N8.DispZ;

                _els[i].ElsDisp = HelsUU;
                _els[i].ElsStress = _els[i].D * _els[i].B * _els[i].ElsDisp;
            }
        }
    }
}

void System::calculateVonMises(Element *_els) const//用盛金公式求 蜂蜜塞斯应力
{
    if(Dimen == 2)//二维坐标下 z的主应力为0 且 zx zy的剪力为0
    {
        for(int i = 0; i < lengthels; ++i)
        {
            double x = _els[i].ElsStress(0,0);
            double y = _els[i].ElsStress(1,0);
            double z = 0;
            double xy = _els[i].ElsStress(2,0);
            double yz = 0;
            double zx = 0;

            double I1 = -(x+y+z);
            double I2 = (x*y+y*z+z*x)-xy*xy-yz*yz-zx*zx;
            double I3 = -(x*y*z + 2*xy*yz*zx -x*yz*yz -y*zx*zx -z*xy*xy);

            double A = I1*I1-3*I2;
            double B = I1*I2-9*I3;
            double C = I2*I2-3*I1*I3;
            double delta = B*B-4*A*C;

            double X[3];

            if ((A == 0)&&(B == 0))
            {
                X[0] = -I1/3;
                X[1] = -I2/I1;
                X[2] = -3*I3/I2;
            }

            else if (delta>0)
            {
//                double Y1 = A*I1+3*((-B+))
//
//                double X1 = -();
//                double X2 = -I2/I1;
//                double X3 = -3*I3/I2;
                cout<<"单元"<<i<<"第一第二第三主应力求解所得是虚数解"<<endl;
            }
            else if (delta == 0)
            {
                double K = B/A;
                X[0] = -I1 + K;
                X[1] = -K/2;
                X[2] = -K/2;
            }
            else if (delta < 0)
            {
                if (A > 0)
                {
                    double T = (2 * A * I1 - 3 * B) / (2 * sqrt(A * A * A));
                    double O = acos(T);
                    X[0] = (-I1 - 2 * sqrt(A) * cos(O / 3)) / 3;
                    X[1] = (-I1 + sqrt(A) * (cos(O / 3) + sqrt(3) * sin(O / 3))) / 3;
                    X[2] = (-I1 + sqrt(A) * (cos(O / 3) - sqrt(3) * sin(O / 3))) / 3;
                }
                else
                {
                    cout <<"单元"<<i<< "A<0 无法解方程组" << endl;
                }
            }
            sort(X,3);//进行排序 由大到小
            double VonMises = (sqrt( (X[0]-X[1])*(X[0]-X[1]) + (X[1]-X[2])*(X[1]-X[2]) + (X[2]-X[0])*(X[2]-X[0]) ))/sqrt(2);
            _els[i].VonMises(0,0) = X[0];
            _els[i].VonMises(1,0) = X[1];
            _els[i].VonMises(2,0) = X[2];
            _els[i].VonMises(3,0) = VonMises;
        }
    }
    if (Dimen == 3)//三维坐标下计算
    {
        for(int i = 0; i < lengthels; ++i)
        {
            double x = _els[i].ElsStress(0,0);
            double y = _els[i].ElsStress(1,0);
            double z = _els[i].ElsStress(2,0);
            double xy = _els[i].ElsStress(3,0);
            double yz = _els[i].ElsStress(4,0);
            double zx = _els[i].ElsStress(5,0);

            double I1 = -(x+y+z);
            double I2 = (x*y+y*z+z*x)-xy*xy-yz*yz-zx*zx;
            double I3 = -(x*y*z + 2*xy*yz*zx -x*yz*yz -y*zx*zx -z*xy*xy);

            double A = I1*I1-3*I2;
            double B = I1*I2-9*I3;
            double C = I2*I2-3*I1*I3;
            double delta = B*B-4*A*C;

            double X[3];

            if ((A == 0)&&(B == 0))
            {
                X[0] = -I1/3;
                X[1] = -I2/I1;
                X[2] = -3*I3/I2;
            }

            else if (delta>0)
            {
//                double Y1 = A*I1+3*((-B+))
//
//                double X1 = -();
//                double X2 = -I2/I1;
//                double X3 = -3*I3/I2;
                cout<<"单元"<<i<<"第一第二第三主应力求解所得是虚数解"<<endl;
            }
            else if (delta == 0)
            {
                double K = B/A;
                X[0] = -I1 + K;
                X[1] = -K/2;
                X[2] = -K/2;
            }
            else if (delta < 0)
            {
                if (A > 0)
                {
                    double T = (2 * A * I1 - 3 * B) / (2 * sqrt(A * A * A));
                    double O = acos(T);
                    X[0] = (-I1 - 2 * sqrt(A) * cos(O / 3)) / 3;
                    X[1] = (-I1 + sqrt(A) * (cos(O / 3) + sqrt(3) * sin(O / 3))) / 3;
                    X[2] = (-I1 + sqrt(A) * (cos(O / 3) - sqrt(3) * sin(O / 3))) / 3;
                }
                else
                {
                    cout <<"单元"<<i<< "A<0 无法解方程组" << endl;
                }
            }
            sort(X,3);//进行排序
            double VonMises = (sqrt( (X[0]-X[1])*(X[0]-X[1]) + (X[1]-X[2])*(X[1]-X[2]) + (X[2]-X[0])*(X[2]-X[0]) ))/sqrt(2);
            _els[i].VonMises(0,0) = X[0];
            _els[i].VonMises(1,0) = X[1];
            _els[i].VonMises(2,0) = X[2];
            _els[i].VonMises(3,0) = VonMises;
        }
    }
}




//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================








using namespace::Eigen;
using namespace::std;
int CountLines(char *filename);//获取txt的行数!!!
[[maybe_unused]] NodeBase * Trans_TXT_to_Node_3D(char *filename);
[[maybe_unused]] void Trans_TXT_CONST_to_Node(char *filename,NodeBase *nds,double x,double y,double z);//从txt传递载荷信息到各个单元

[[maybe_unused]] Tetra3D4N * Trans_Node_to_Tetra3D4N3DOF(char *filename,NodeBase *nds);
[[maybe_unused]] Hexah3D8N * Trans_Node_to_Hexah3D8N3DOF(char *filename,NodeBase *nds);

[[maybe_unused]] void Trans_ENU_to_Hexah3D6N3DOF(Hexah3D8N* els,double E,double NU);
[[maybe_unused]] void Trans_ENU_to_Tetra3D4N3DOF(Tetra3D4N* els,double E,double NU);
int main([[maybe_unused]] int argc, [[maybe_unused]] const char * argv[])
{
//    int NDSNUM = 131;
//    NodeBase nds[NDSNUM];
//    int Force[] = {5,6,7,33};
//    ifstream ndsfile("/Users/zhangdailin/CLionProjects/new/nds.txt");
//    ifstream elsfile("/Users/zhangdailin/CLionProjects/new/els.txt");
//    ifstream consfile("/Users/zhangdailin/CLionProjects/new/con.txt");
//    double ndsnum[131][4];
//    int consnum[55];
//    int elsnum[350][4];
//    double E = 2.1e5;
//    double NU = 0.31;
//    Tetra3D4N Tels[350];
//    Element els[350];
//
//    TransNDS3D(ndsfile,ndsnum,nds);
//    TransNDStoCONST(consfile,consnum,nds,1,1,1);
//    for (int i : Force) {
//        nds[i-1].inputLoad3D(0,0,25);
//    }
//    TransNdsToTetra3D4N(elsfile,elsnum,Tels,nds);
//    Tetra3D4NInputENUT(Tels,E,NU);
//    for (int i = 0; i < Tels[0].ClassElsNum; i++) {
//        els[i] = Tels[i];
//    }
//    System solve;
//    solve.inputels(els,nds);








//    int Force[] = {5,6,7,33};
//
//    char ndstxt[] = "/Users/zhangdailin/CLionProjects/testAnsys/nds.txt";
//    char elstxt[] = "/Users/zhangdailin/CLionProjects/testAnsys/els.txt";
//    char contxt[] = "/Users/zhangdailin/CLionProjects/testAnsys/con.txt";
//
//    NodeBase *nds = Trans_TXT_to_Node_3D(ndstxt);
//
//    //载荷和约束信息必须在单元添加节点信息前添加到节点信息中
//    Trans_TXT_CONST_to_Node(contxt,nds,1,1,1);
//    for (int i : Force)
//    {
//        nds[i-1].inputLoad3D(0,0,25000);
//    }
//
//    Tetra3D4N *Tels = Trans_Node_to_Tetra3D4N3DOF(elstxt,nds);
//    Trans_ENU_to_Tetra3D4N3DOF(Tels,1e5,0.3);
//
//    Element els[Tels[0].ClassElsNum];
//    for (int i = 0; i < Tels[0].ClassElsNum; i++)
//    {
//        els[i] = Tels[i];
//    }
//
//
//    System solve;
//    solve.inputels(els,nds);





//    NodeBase nds[4];
//    nds[0].inputCoord2D(1,2,1);
//    nds[1].inputCoord2D(2,2,0);
//    nds[2].inputCoord2D(3,0,1);
//    nds[3].inputCoord2D(4,0,0);
//    nds[2].inputConstraint2D(1,1);
//    nds[3].inputConstraint2D(1,1);
//    nds[0].inputLoad2D(0,-5e4);
//    nds[1].inputLoad2D(0,-5e4);
//    Tria2D3N Tels[2];
//    Tels[0].inputNode(nds[1],nds[2],nds[3]);
//    Tels[1].inputNode(nds[2],nds[1],nds[0]);
//    Tels[0].inputENUT(1e7,0.33333333,0.1);
//    Tels[1].inputENUT(1e7,0.33333333,0.1);
//    Element els[2];
//    els[0] = Tels[0];
//    els[1] = Tels[1];
//    System solve;
//    solve.inputels(els,nds);

    NodeBase nds[8];
    Hexah3D8N Hels[1];

    nds[0].inputCoord3D(1,0.2,0,0);
    nds[1].inputCoord3D(2,0.2,0.8,0);
    nds[2].inputCoord3D(3,0,0.8,0);
    nds[3].inputCoord3D(4,0,0,0);
    nds[4].inputCoord3D(5,0.2,0,0.6);
    nds[5].inputCoord3D(6,0.2,0.8,0.6);
    nds[6].inputCoord3D(7,0,0.8,0.6);
    nds[7].inputCoord3D(8,0,0,0.6);

    nds[0].inputConstraint3D(1,1,1);
    nds[3].inputConstraint3D(1,1,1);
    nds[4].inputConstraint3D(1,1,1);
    nds[7].inputConstraint3D(1,1,1);

    nds[5].inputLoad3D(0,0,-1e7);
    nds[6].inputLoad3D(0,0,-1e7);

    Hels[0].inputNode(nds[0],nds[1],nds[2],nds[3],nds[4],nds[5],nds[6],nds[7]);

    Hels[0].inputENUT(1e10,0.25);

    System solve;
    solve.inputels(Hels,nds);
    cout<<Hels[0].K<<endl;

//    NodeBase nds1[8];
//    Hexah3D8N els[1];
//
//    nds1[0].inputCoord3D(1,0.2,0,0);
//    nds1[1].inputCoord3D(2,0.2,0.8,0);
//    nds1[2].inputCoord3D(3,0,0.8,0);
//    nds1[3].inputCoord3D(4,0,0,0);
//    nds1[4].inputCoord3D(5,0.2,0,0.6);
//    nds1[5].inputCoord3D(6,0.2,0.8,0.6);
//    nds1[6].inputCoord3D(7,0,0.8,0.6);
//    nds1[7].inputCoord3D(8,0,0,0.6);
//
//    els[0].inputNode(nds[0],nds[1],nds[2],nds[3],nds[4],nds[5],nds[6],nds[7]);
//
//    els[0].inputENUT(1e10,0.25);
//
//    cout<<"\n\n"<<els[0].K<<endl;


//
//    cout<<sqrt(4)<<endl;
//    char ndstxt[] = "/Users/zhangdailin/CLionProjects/testAnsys/ndstxt";
//    char elstxt[] = "/Users/zhangdailin/CLionProjects/testAnsys/elstxt";
//    NodeBase *nds = Trans_TXT_to_Node_3D(ndstxt);
//
//    int CONS[] = {6,7,8,14,15,18,19,31,32};
//    int FORCE[] = {1,2,3,21,24,25,28,33,35};
//
//    for (int i = 0; i <9; i++)
//    {
//        nds[CONS[i]-1].inputConstraint3D(1,1,1);
//    }
//
//    for (int i : FORCE)
//    {
//        nds[i-1].inputLoad3D(0,0,-1000);
//    }
//
//    Hexah3D8N *Hels = Trans_Node_to_Hexah3D8N3DOF(elstxt,nds);
//
//    Trans_ENU_to_Hexah3D6N3DOF(Hels,2e7,0.3);
//    Element els[Hels[0].ClassElsNum];
//    for (int i = 0; i < Hels[0].ClassElsNum; i++)
//    {
//        els[i] = Hels[i];
//    }
//    System solve;
//    solve.inputels(els,nds);





}

//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================







[[maybe_unused]] NodeBase * Trans_TXT_to_Node_3D(char *filename)//将txt文件中的X Y Z 坐标传入到各个节点中
{
    ifstream ndsfile;
    ndsfile.open(filename,ios::in);//以只读的方式打开
    if(ndsfile.fail())//文件打开失败:返回0
    {
        cout<<"节点文件打开失败"<<endl;
    }
    //或许文件行数 关于temp我也不清楚为什么这么用
    int lines  = CountLines(filename);
    /*
    若不加NEW 在重复调用此函数的时候 指针会被重新的分配
    C++ 提供了一种“动态内存分配”机制，使得程序可以在运行期间，根据实际需要，要求操作系统临时分配一片内存空间用于存放数据。
    此种内存分配是在程序运行中进行的，而不是在编译时就确定的，因此称为“动态内存分配”。
     NodeBase* nds = new NodeBase[lines];
     相当于在系统中开辟了一块新的内存空间 用来存放数据 空间大小为 lines*sizeof(Nodebase)
    */
    auto* nds = new NodeBase[lines];

    cout<<"节点总数："<<lines<<endl;
    double ndsnum[lines][4];
    for (auto & i : ndsnum)
    {
        for (double & j : i)
        {
            ndsfile>>j;
        }
    }
    //批量读取节点
    for (int i = 0; i < lines ; i++)
    {
        nds[i].inputCoord3D(int(ndsnum[i][0]),ndsnum[i][1],ndsnum[i][2],ndsnum[i][3]);
    }
    return nds;

    delete[] nds;
    //将刚刚动态分配出的新内存删除掉
}



[[maybe_unused]] NodeBase * Trans_TXT_to_Node_2D(char *filename)//将txt文件中的X Y 坐标传入到各个节点中
{
    ifstream ndsfile;
    ndsfile.open(filename,ios::in);//以只读的方式打开
    if(ndsfile.fail())//文件打开失败:返回0
    {
        cout<<"节点文件打开失败"<<endl;
    }
    //或许文件行数 关于temp我也不清楚为什么这么用
    int lines  = CountLines(filename);

    auto* nds = new NodeBase[lines];

    cout<<"节点总数："<<lines<<endl;
    double ndsnum[lines][3];
    for (auto & i : ndsnum)
    {
        for (double & j : i)
        {
            ndsfile>>j;
        }
    }
    //批量读取节点
    for (int i = 0; i < lines ; i++)
    {
        nds[i].inputCoord2D(int(ndsnum[i][0]),ndsnum[i][1],ndsnum[i][2]);
    }
    return nds;
    delete[] nds;
    //将刚刚动态分配出的新内存删除掉
}



[[maybe_unused]]void Trans_TXT_CONST_to_Node(char *filename,NodeBase *nds,double x,double y,double z)//加载约束
{
    ifstream consfile;
    consfile.open(filename,ios::in);
    int lines = CountLines(filename);

    int consnum[lines];

    if (!consfile.is_open())
    {
        cout << "can not open this file" << endl;
    }
    for (auto &i : consnum)
    {
        consfile>>i;
    }

    for (auto &i : consnum)
    {
        nds[i-1].inputConstraint3D(x,y,z);
    }
}

//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================

//四面体批量读取节点信息

[[maybe_unused]] Tetra3D4N * Trans_Node_to_Tetra3D4N3DOF(char *filename,NodeBase *nds)
{
    ifstream elsfile;
    elsfile.open(filename,ios::in);
    int lines  = CountLines(filename);
    cout<<"四面体单元总数："<<lines<<endl;

    int elsnum[lines][4];

    auto *Tels = new Tetra3D4N[lines];
    if (!elsfile.is_open())
    {
        cout << "can not open this file" << endl;
    }
    for (int i = 0; i < sizeof(elsnum)/sizeof(elsnum[0]); i++)
    {
        for (int j = 0; j < 4; j++)
        {
            elsfile>>elsnum[i][j];
        }
    }
    for (int i = 0; i < sizeof(elsnum)/sizeof(elsnum[0]); i++)
    {
        Tels[i].inputNode(nds[elsnum[i][0]-1],nds[elsnum[i][1]-1],nds[elsnum[i][2]-1],nds[elsnum[i][3]-1]);
    }

    return Tels;
    delete[] Tels;
}








//六面体批量读取节点信息
[[maybe_unused]] Hexah3D8N * Trans_Node_to_Hexah3D8N3DOF(char *filename,NodeBase *nds)
{
    ifstream elsfile;
    elsfile.open(filename,ios::in);
    int lines  = CountLines(filename);
    cout<<"六面体单元总数："<<lines<<"\n"<<endl;
    int elsnum[lines][8];
    auto *Hels = new Hexah3D8N[lines];

    if (!elsfile.is_open())
    {
        cout << "can not open this file" << endl;
    }
    for (auto &i : elsnum)
    {
        for (int &j : i)
        {
            elsfile>>j;
        }
    }
    for (int i = 0; i <lines ; i++)
    {
        Hels[i].inputNode(nds[elsnum[i][0]-1],nds[elsnum[i][1]-1],nds[elsnum[i][2]-1],nds[elsnum[i][3]-1],nds[elsnum[i][4]-1],nds[elsnum[i][5]-1],nds[elsnum[i][6]-1],nds[elsnum[i][7]-1]);
    }
    return Hels;
    delete[] Hels;
}

//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================

//如何修改成template？

[[maybe_unused]] void Trans_ENU_to_Tetra3D4N3DOF(Tetra3D4N* els,double E,double NU)
{
    for (int i = 0;i<Tetra3D4N::Elsindex;i++)
    {
        els[i].inputENUT(E,NU);
    }
}


[[maybe_unused]] void Trans_ENU_to_Quad2D4N2DOF(Quad2D4N* els,double E,double NU,double T)//T为厚度
{
    for (int i = 0;i<Quad2D4N::Elsindex;i++)
    {
        els[i].inputENUT(E,NU,T);
    }
}



[[maybe_unused]] void Trans_ENU_to_Tria2D3N2DOF(Tria2D3N* els, double E, double NU, double T)
{
    for (int i = 0;i<Tria2D3N::Elsindex;i++)
    {
        els[i].inputENUT(E,NU,T);
    }
}



[[maybe_unused]] void Trans_ENU_to_Hexah3D6N3DOF(Hexah3D8N* els,double E,double NU)
{
    for (int i = 0;i<Hexah3D8N::Elsindex;i++)
    {
        els[i].inputENUT(E,NU);
    }
}


//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================
//=======================================================================================================================================================================


int CountLines(char *filename)//获取txt的行数!!!
{
    ifstream ReadFile;
    int n=0;
    string temp;
    ReadFile.open(filename,ios::in);//ios::in 表示以只读的方式读取文件
    if(ReadFile.fail())//文件打开失败:返回0
    {
        return 0;
    }
    else//文件存在,返回文件行数
    {
        while(getline(ReadFile,temp))
        {
            n++;
            if (temp.empty())//进行修正
            {
                n--;
            }
        }
        return n;
    }
    ReadFile.close();
}
