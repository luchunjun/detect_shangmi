package com.lu.portable.detect;

import java.util.HashMap;
import java.util.Map;

public class MyLineRegression {
    /**
     * 最小二乘法
     * @param X
     * @param Y
     * @return y = ax + b, r
     */
    public Map<String, Double> lineRegression(double[] X, double[] Y)
    {
        if(null == X || null == Y || 0 == X.length
                || 0 == Y.length || X.length != Y.length)
        {
            throw new RuntimeException();
        }

        // x平方差和
        double Sxx = varianceSum(X);
        // y平方差和
        double Syy = varianceSum(Y);
        // xy协方差和
        double Sxy = covarianceSum(X, Y);

        double xAvg = arraySum(X) / X.length;
        double yAvg = arraySum(Y) / Y.length;

        double a = Sxy / Sxx;
        double b = yAvg - a * xAvg;

        // 相关系数
        double r = Sxy / Math.sqrt(Sxx * Syy);
        Map<String, Double> result = new HashMap<>();
        result.put("k", a);
        //result.put("b", b);
        result.put("r", r);

        return result;
    }

    /**
     * 计算方差和
     * @param X
     * @return
     */
    private double varianceSum(double[] X)
    {
        double xAvg = arraySum(X) / X.length;
        return arraySqSum(arrayMinus(X, xAvg));
    }

    /**
     * 计算协方差和
     * @param X
     * @param Y
     * @return
     */
    private double covarianceSum(double[] X, double[] Y)
    {
        double xAvg = arraySum(X) / X.length;
        double yAvg = arraySum(Y) / Y.length;
        return arrayMulSum(arrayMinus(X, xAvg), arrayMinus(Y, yAvg));
    }

    /**
     * 数组减常数
     * @param X
     * @param x
     * @return
     */
    private double[] arrayMinus(double[] X, double x)
    {
        int n = X.length;
        double[] result = new double[n];
        for(int i = 0; i < n; i++)
        {
            result[i] = X[i] - x;
        }

        return result;
    }

    /**
     * 数组求和
     * @param X
     * @return
     */
    private double arraySum(double[] X)
    {
        double s = 0 ;
        for( double x : X )
        {
            s = s + x ;
        }
        return s ;
    }

    /**
     * 数组平方求和
     * @param X
     * @return
     */
    private double arraySqSum(double[] X)
    {
        double s = 0 ;
        for( double x : X )
        {
            s = s + Math.pow(x, 2) ; ;
        }
        return s ;
    }

    /**
     * 数组对应元素相乘求和
     * @param X
     * @return
     */
    private double arrayMulSum(double[] X, double[] Y)
    {
        double s = 0 ;
        for( int i = 0 ; i < X.length ; i++ )
        {
            s = s + X[i] * Y[i] ;
        }
        return s ;
    }

    public static void main(String[] args)
    {
       // Random random = new Random();
        double[] Y = {300,50,9000,12000,3000,0,500,15000,100,6000,1000,800};
        double[] X = {575846.83,469277.75,4192786.47,5439136.38,1708948.18,445792.3,663467.56,6681587.83,489703.1,2947243.43,876841.22,851925.44};


//        for(int i = 0; i < 20; i++)
//        {
//            X[i] = Double.valueOf(Math.floor(random.nextDouble() * 97));
//            Y[i] = Double.valueOf(Math.floor(random.nextDouble() * 997));
//        }

        System.out.println(new MyLineRegression().lineRegression(X, Y));
    }

}
