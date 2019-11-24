package com.example.crossline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Random;

public class ViewEx extends View {

    Point p1, p2, p3, p4;   //선분 2개를 나타낼 점 4개

    Paint mPaint = new Paint();
    int x=0, y=0;   //터치시 입력되는 x, y 좌표
    int[] xL = new int[5];  //터치시 입력되는 x좌표값이 저장되는 배열
    int[] yL = new int[5];  //터치시 입력되는 y좌표값이 저장되는 배열
    int[][] lL = new int[4][4]; //DrawLine에 들어가는 x,y좌표값 저장, 쉽게 말해서 선분을 저장하는 배열
    int[] xLcpy = new int[5];   //버튼터치시 랜덤으로 x좌표값 저장
    int[] yLcpy = new int[5];   //버튼터치시 랜덤으로 y좌표값 저장
    int[] visit = new int[5];   //랜덤함수 돌릴 시 중복되는 값이 나오지 않게 저장
    int c = 0;  //카운트(1)
    int cpy = 0;    //카운트(2)
    String b = "BUTTON";    //버튼 이름
    Boolean mBoolean = false;   //선을 그렸는지 안그렸는지 나타내는 변수
    Boolean lineCross = false;  //선분이 교차되는지 안되는지 판단하는 논리변수
    Random random = new Random();
    Button button;
    MainActivity mainActivity;

    public ViewEx(Context context, AttributeSet attr){
        super(context, attr);
        mPaint.setColor(Color.BLACK);   //그려질 색상 설정
        mPaint.setStrokeWidth(30f);     //그려질 두께 설정
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onDraw(Canvas canvas){
        final Canvas mCanvas = canvas;  // 캔버스 생성

        button = new Button(mainActivity);

        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(lp);
        button.setText(b);
        ((ConstraintLayout)this.getParent()).addView(button);

        mCanvas.drawColor(Color.WHITE);     //캔버스 색상설정

            for (int i = 0; i < c; i++) {       //터치한 곳에 점 찍기
                mCanvas.drawPoint(xL[i], yL[i], mPaint);
            }

            button.setOnClickListener(new OnClickListener() {   //버튼을 누를 때 선분 생성
                @Override
                public void onClick(View view) {
                    mBoolean = true;    //버튼을 누른 후 선분이 나오게 설정
                    lineCross = false;  //버튼을 누른 후 교차선분변수 초기화

                    for (int i = 0; i < c ; i++) {  //버튼 누를 때 마다 좌표값을 랜덤으로 배치 후 다시 선 연결
                        cpy = (random.nextInt(c));  // 랜덤인덱스 선택
                        if (visit[cpy] > 0) {   //이미 선택된 인덱스일 경우 다시 선택
                            --i;
                            continue;
                        }
                        visit[cpy]++;

                        xLcpy[i] = xL[cpy]; //랜덤인덱스의 값을 x좌표 배열에 입력
                        yLcpy[i] = yL[cpy]; //랜덤인덱스의 값을 y좌표 배열에 입력
                    }

                    for(int i = 0; i < c-1; i++){   //새로 배열된 x,y 값들을 선분배열에 저장
                        lL[i][0] = xLcpy[i];
                        lL[i][1] = yLcpy[i];
                        lL[i][2] = xLcpy[i+1];
                        lL[i][3] = yLcpy[i+1];
                    }

                    for(int i = 0; i < c-2; i++){
                        for(int j = 1; j < c-1; j++){
                            selectLine(lL, i, j);       //이중반복문을 활용해 모든 선분의 교차여부 확인
                            if(lineCross){      //선택된 선분이 교차됐을경우 Toast로 알려주면서 선분 제거
                                Toast.makeText(mainActivity, "선분이 교차됩니다. 다시 눌러주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        if(lineCross) break;
                    }

                    for (int i = 0; i < c; i++) {
                        visit[i] = 0;   //다음번 사용을 위한 초기화
                    }
                    invalidate();
                }
            });

            if (mBoolean && !lineCross) {   //선분이 교차되지 않았을경우 선분을 나타냄
                for (int i = 0; i < c - 1; i++) {
                    mCanvas.drawLine(lL[i][0], lL[i][1], lL[i][2], lL[i][3], mPaint);   //좌표에 따른 선 생성
                }
            }
    }


    int cCW(Point p1, Point p2, Point p3){  //선분교차 확인을 위한 CCW 알고리즘
        int s = (p1.x * p2.y) + (p2.x * p3.y) + (p3.x * p1.y);
        s -= (p1.y * p2. x) + (p2.y * p3.x) + (p3.y * p1.x);

        if( s > 0) return 1;
        else if (s == 0) return 0;
        else return -1;
    }

    void selectLine(int[][] lL, int cnt1, int cnt2){    //CCW 알고리즘을 활용해 선택된 두 선분의 교차여부 판단

        p1 = new Point(lL[cnt1][0], lL[cnt1][1]);   //점1
        p2 = new Point(lL[cnt1][2], lL[cnt1][3]);   //점2
        p3 = new Point(lL[cnt2][0], lL[cnt2][1]);   //점3
        p4 = new Point(lL[cnt2][2], lL[cnt2][3]);   //점4

        int ccw_1 = cCW(p1, p2, p3) * cCW(p1, p2, p4);
        int ccw_2 = cCW(p3, p4, p1) * cCW(p3, p4, p2);

        lineCross = (ccw_1 < 0) && (ccw_2 < 0);     //두 선분이 교차할 경우 lineCross의 값이 true
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){ //터치시 점의 좌표 입력과 동시에 점배열, 선분배열에 값 저장
        super.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            x = (int)event.getX();
            y = (int)event.getY();
            if(c < 5){
                xL[c] = x;
                yL[c] = y;
                if(0 < c){
                    lL[c-1][0] = xL[c-1];
                    lL[c-1][1] = yL[c-1];
                    lL[c-1][2] = xL[c];
                    lL[c-1][3] = yL[c];
                }
                c++;
            }
        }
        invalidate();
        return true;
    }
}