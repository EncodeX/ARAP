package com.edu.neu.navigation.util;

import com.edu.neu.navigation.Enum.Instruction;
import com.edu.neu.navigation.Enum.Orientation;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/7/6.
 */



public class NavigationUtil {
    public void getNavigation(Point currentPoint, Point destinationPoint, List<Point> pass, double currentDegree)
    {

    }
    public List<Instruction> getNavigation(Point currentPoint, Point destinationPoint, double currentDegree)
    {

        int limit =20;
        //屏幕方向朝北的情况
        List<Orientation> originalUserOrientation = new ArrayList<>();
//        if(destinationPoint.x-currentPoint.x>limit)
//        {
//            originalUserOrientation.add(Orientation.EAST);
//        }
//        else if(currentPoint.x -destinationPoint.x>limit)
//        {
//            originalUserOrientation.add(Orientation.WEST);
//        }
//        if(destinationPoint.y-currentPoint.y>limit)
//        {
//            originalUserOrientation.add(Orientation.SOUCE);
//        }
//        else if(currentPoint.y -destinationPoint.y>limit)
//        {
//            originalUserOrientation.add(Orientation.NOTRH);
//        }

        //实际屏幕中朝向西

        if(destinationPoint.x-currentPoint.x>limit)
        {
            originalUserOrientation.add(Orientation.NOTRH);
        }
        else if(currentPoint.x -destinationPoint.x>limit)
        {
            originalUserOrientation.add(Orientation.SOUCE);
        }
        if(destinationPoint.y-currentPoint.y>limit)
        {
            originalUserOrientation.add(Orientation.EAST);
        }
        else if(currentPoint.y -destinationPoint.y>limit)
        {
            originalUserOrientation.add(Orientation.WEST);
        }
        List<Instruction> realInstruction =getRealInstruction(originalUserOrientation,currentDegree);
        return realInstruction;



    }
    private List<Instruction> getRealInstruction(List<Orientation> originalUserOrientation, double degree) {
        List<Instruction> realInstruction = new ArrayList<>();
        List<Instruction> allInstructions =new ArrayList<>();
        allInstructions.add(Instruction.STRAIGHT);
        allInstructions.add(Instruction.RIGHT);
        allInstructions.add(Instruction.BACK);
        allInstructions.add(Instruction.LEFT);
        int rotation = getRotation(degree);
        for (Orientation o :originalUserOrientation) {
            int start =0;
            switch (o)
            {
                case NOTRH:
                    start=0;
                    break;
                case EAST:
                    start=1;
                    break;
                case SOUCE:
                    start=2;
                    break;
                case WEST:
                    start=3;
                    break;
            }
            int relativeRotation = (start-rotation)%4;
            if (relativeRotation<0)
            {
                relativeRotation=relativeRotation+4;
            }

            realInstruction.add(allInstructions.get(relativeRotation));

        }

        return realInstruction;
    }
    private int getRotation(double degree)
    {
        if(degree<90&&0<=degree)
        {
            return  0;
        }
        else if(degree<180&&90<=degree)
        {
            return 1;
        }else if(degree<270&&180<=degree)
        {
            return 2;
        }else if(degree<360&&270<=degree)
        {
            return 3;
        }
        return 0;

    }


}
