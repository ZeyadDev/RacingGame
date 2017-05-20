package com.example.almajed.racinggame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }
    public void action(View v){
        Game g = (Game)findViewById(R.id.background);
        switch (v.getId()) {
            case R.id.speedUp:
                g.changeUserSpeed(Speed.speedUp);
                break;
            case R.id.slowDown:
                g.changeUserSpeed(Speed.slowDown);
                break;
        }
    }
}

class Game extends View {
    public final static int numOfCarImages = 4;
    public final static int numberOfSides = 5;
    public final static int maxSpeed = 90, minSpeed = 20;
    public int streetY = 0;
    private int userSpeed = 60;
    private static int canvasWidth,canvasHeight,sideSpace,lineSpace;
    private static  int lineWidth = 20;
    public static Bitmap userCarImage;
    public static Bitmap[] carImages = new Bitmap[numOfCarImages];
    public final int[] carsIDs = {R.drawable.car1,R.drawable.car2,R.drawable.car3,R.drawable.car4};
    private Paint grayPaint,whitePaint,yellowPaint;
    public Canvas canvas;
    private Context context;
    private boolean oneTime = true;
    public Game(Context c, @Nullable AttributeSet attrs) {
        super(c, attrs);
        context = c;

        grayPaint = new Paint();
        grayPaint.setColor(Color.GRAY);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setTextSize(30);
        Button button = new Button(context);

    }
    @Override
    protected void onDraw(Canvas canvas){
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        sideSpace = canvasWidth/numberOfSides;
        lineSpace = canvasHeight/10;
        if(oneTime) {
            Resources resources = context.getResources();
            userCarImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.car), sideSpace, 2 * sideSpace, false);
            for (int i = 0; i < carImages.length; i++)
                carImages[i] = BitmapFactory.decodeResource(resources, carsIDs[i]);
            oneTime = false;
        }
        drawStreet(canvas);
        drawUserCar(canvas);
        this.canvas = canvas;
        invalidate();
    }
    private void drawStreet(Canvas canvas){
        canvas.drawRect(new Rect(0,0,canvasWidth,canvasHeight),grayPaint);
        canvas.drawRect(new Rect(0,0,lineWidth,canvasHeight),yellowPaint);
        canvas.drawRect(new Rect(canvasWidth-lineWidth,0,canvasWidth,canvasHeight),yellowPaint);
        streetY += userSpeed;
        for(int i = -2; i < 10; i += 2) {
            for(int j = 1; j <= numberOfSides; j++)
                canvas.drawRect(new Rect(j * sideSpace, streetY + i * lineSpace, j * sideSpace + lineWidth,streetY + (i + 1) * lineSpace), whitePaint);
        }
        if(streetY > 2 * lineSpace)
            streetY = 0;
    }
    private void drawUserCar(Canvas canvas){
        canvas.drawBitmap(userCarImage,sideSpace,canvasHeight - userCarImage.getHeight()-10,new Paint());
    }
    public void changeUserSpeed(Speed speed){
        int s = 1;
        switch (speed){
            case speedUp:
                s = 1;
                break;
            case slowDown:
                s = -1;
                break;
        }
        userSpeed +=  s * 5;
        if(userSpeed < minSpeed)
            userSpeed = minSpeed;
        else if(userSpeed > maxSpeed)
            userSpeed = maxSpeed;
    }
}
enum Speed {
    speedUp, slowDown
}

/*  private static double yBackground = 0;
    private UserCar userCar = new UserCar();
    private StreetCar[] cars = new StreetCar[numOfCarImages];
    private CarsManager manager = new CarsManager(userCar, cars);
    private int score = 0;

    public Game() {
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new StreetCar(manager);
        }
        setLayout(null);
        Font font = new Font("Arial", Font.BOLD, 25);
        speedLabel = new JLabel("Speed: " + 0 + " k/m");
        speedLabel.setBounds(10, 10, 300, 30);
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(font);

        scoreLabel = new JLabel("Score: " + 0);
        scoreLabel.setBounds(windowWidth - 10 - 170, 10, 170, 30);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(font);

        add(speedLabel);
        add(scoreLabel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawOval(100, 100, 200, 150);
        g.drawImage(gameBackground, 0, (int) (yBackground - windowHeight + 1), windowWidth, windowHeight, null);
        g.drawImage(gameBackground, 0, (int) yBackground, windowWidth, windowHeight, null);
        yBackground += Car.userSpeed / 100;
        if (yBackground > windowHeight) {
            yBackground = 0;
            score += 10;
        }
        userCar.draw(g);
        speedLabel.setText("Speed: " + (int) (userCar.getSpeed() / 2) + " k/m");
        scoreLabel.setText("Score: " + (int) (score + 10 * yBackground / windowHeight));
        for (int i = 0; i < cars.length; i++)
            cars[i].draw(g);
        manager.checkStreet();
        if (manager.isThereCarAccident()) {
            JOptionPane.showMessageDialog(null, "Game Over", "Game Over !", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_RIGHT:
                userCar.setDirection(Direction.Right);
                break;
            case KeyEvent.VK_LEFT:
                userCar.setDirection(Direction.Left);
                break;
            case KeyEvent.VK_UP:
                userCar.changeSpeed(Speed.speedUp);
                break;
            case KeyEvent.VK_DOWN:
                userCar.changeSpeed(Speed.slowDown);
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
            userCar.setDirection(Direction.Straight);
    }

    public void keyTyped(KeyEvent e) {
    }
}

abstract class Car {
    protected double x, y, xd, yd, speed;
    protected static int carsWidth = 70, carsHeight = 130;
    protected BufferedImage image;
    public static double userSpeed;

    public Car(double x, double y, double xd, double yd, double speed, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.xd = xd;
        this.yd = yd;
        this.speed = speed;
        this.image = image;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getXd() {
        return xd;
    }

    public void setXd(double xd) {
        this.xd = xd;
    }

    public double getYd() {
        return yd;
    }

    public void setYd(double yd) {
        this.yd = yd;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, carsWidth, carsHeight);
    }

    public void draw(Graphics g) {
        g.drawImage(image, (int) x, (int) y, carsWidth, carsHeight, null);
        x += xd * 2.5;
        y += yd * (userSpeed - speed) / 100;
    }
}

class StreetCar extends Car {
    public CarsManager manager;

    public StreetCar(CarsManager m) {
        super(0, 0, 0, 1, 0, null);
        manager = m;
        setRandomVariables();
    }

    public void draw(Graphics g) {
        super.draw(g);
        if (y > RacingGame.windowHeight * 1.2) {
            setRandomVariables();
        }
    }

    private void setRandomVariables() {
        int[] sides = RacingGame.sides;
        BufferedImage[] carImages = RacingGame.carImages;
        int count = 0;
        do {
            x = sides[(int) (sides.length * Math.random())];
            y = -carsHeight * (1.3 + count);
            count++;
        } while (manager.isItNearOfOthers(this));

        speed = 100 * (1 + 0.7 * Math.random());
        image = carImages[(int) (carImages.length * Math.random())];
    }

    public void ensureSafeDistance(StreetCar c) {
        if (c.x == x) {
            if (c.y <= y + carsHeight + 50)
                c.speed = speed;
            else if (y >= c.y + carsHeight + 50)
                speed = c.speed;
        }
    }

    public boolean isItNearOfACar(StreetCar c) {
        boolean b = false;
        if (c.x == x)
            if (c.y <= y + carsHeight + 50 && c.y >= y - carsHeight - 50)
                b = true;
        return b;
    }
}

enum Direction {
    Right, Left, Straight
}

class UserCar extends Car {
    private final static int maxSpeed = 500;
    private final static int minSpeed = 100;
    private final double speedUpAmount = 15;
    private final double slowDownAmount = 30;
    private final static int intialSpeed = 120;
    public static Direction direction = Direction.Straight;

    public UserCar() {
        super(RacingGame.windowWidth / 2 - carsWidth / 2, RacingGame.windowHeight - carsHeight - 140, 0, 0, intialSpeed,
                RacingGame.userCarImage);
        userSpeed = intialSpeed;
    }

    public void setDirection(Direction d) {
        direction = d;
    }

    public void changeSpeed(Speed s) {
        double velocity = 0;
        switch (s) {
            case speedUp:
                velocity = -speedUpAmount / maxSpeed * speed + 15;
                speed += velocity;
                break;
            case slowDown:
                speed -= slowDownAmount;
                break;
        }
        if (speed < minSpeed)
            speed = minSpeed;
        else if (speed > maxSpeed)
            speed = maxSpeed;
        userSpeed = speed;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (x < RacingGame.streetLeft)
            x = RacingGame.streetLeft;
        else if (x > RacingGame.streetRight)
            x = RacingGame.streetRight;
        switch (direction) {
            case Right:
                xd = 1;
                break;
            case Left:
                xd = -1;
                break;
            case Straight:
                xd = 0;
                break;
        }
    }
}

class CarsManager {
    private UserCar userCar;
    private StreetCar[] cars;
    private boolean stopped = false;

    public CarsManager(UserCar u, StreetCar[] c) {
        userCar = u;
        cars = c;
    }

    public boolean isThereCarAccident() {
        Rectangle userRec = userCar.getRectangle();
        for (int i = 0; i < cars.length; i++) {
            if (userRec.intersects(cars[i].getRectangle()))
                return true;
        }
        return false;
    }

    public void checkStreet() {
        final double x = userCar.x;
        final double y = userCar.y;
        final int width = Car.carsWidth;
        final int height = Car.carsHeight;
        for (int i = 0; i < cars.length; i++) {
            if (cars[i] != null && userCar != null && cars[i].y >= y + height &&
                    cars[i].speed > userCar.speed &&
                    ((cars[i].x >= x && cars[i].x <= x + width)
                            || (cars[i].x <= x && cars[i].x + width >= x)))
                cars[i].speed = userCar.speed;
            for (int j = i + 1; j < cars.length; j++)
                cars[i].ensureSafeDistance(cars[j]);
        }
    }

    public boolean isItNearOfOthers(StreetCar c) {
        for (int i = 0; i < cars.length; i++)
            if (c != null && cars[i] != null && c != cars[i] && cars[i].isItNearOfACar(c))
                return true;
        return false;
    }
}*/