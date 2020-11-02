package com.example.univirtuscalculator;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.Float.parseFloat;

public class MainActivity2 extends AppCompatActivity{

    TextView display;
    String displayNumber;
    Float operand = null;
    Float operator = null;
    private Method operationMethod;
    private MathOperations mathOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        display = findViewById(R.id.textView);
        displayNumber = new String();
        mathOperations = new MathOperations();

        //Button number0 = (Button) findViewById(R.id.btnNumber0);
        //number0.setOnClickListener(numberListener);

        Button clear = (Button) findViewById(R.id.btnClear);
        clear.setOnClickListener(clearListener);

        Button dot = (Button) findViewById(R.id.btnDot);
        dot.setOnClickListener(dotListener);

        Button equal = (Button) findViewById(R.id.btnEquals);
        equal.setOnClickListener(operationResultListener);

        for (Field field : R.id.class.getFields()) {
            try {
                int fieldId = field.getInt(0);
                if(field.getName().startsWith("btnNumber"))
                {
                    Button number = null;
                    number = (Button) findViewById(fieldId);
                    number.setOnClickListener(numberListener);
                }
                else if(field.getName().startsWith("btnOperation"))
                {
                    Button operation = null;
                    operation = (Button) findViewById(fieldId);
                    operation.setOnClickListener(operationListener);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    View.OnClickListener numberListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Button pressedBtn = (Button) view;
            String number = pressedBtn.getText().toString();
            displayNumber += number;
            display.setText(displayNumber);
        }
    };

    View.OnClickListener dotListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Button pressedBtn = (Button) view;
            String dot = pressedBtn.getText().toString();
            if(displayNumber != "" && displayNumber.indexOf(".") == -1)
                displayNumber += dot;
            display.setText(displayNumber);
        }
    };

    View.OnClickListener operationListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Button pressedBtn = (Button) view;
            String operation = pressedBtn.getText().toString();
            String methodName = (String) pressedBtn.getTag();
            try {
                if(operand == null && displayNumber != "") {
                    operand = parseFloat(displayNumber);
                    displayNumber += " " + operation + " ";
                    display.setText(displayNumber);
                    //Set correct operation
                    operationMethod = mathOperations.getClass().getDeclaredMethod(methodName, Float.TYPE, Float.TYPE);
                }
                else if(operand != null && operator == null){
                    int operatorIndex = displayNumber.indexOf(operation);
                    String operatorAsString = displayNumber.substring(operatorIndex+1);

                    if(operatorAsString != "")
                        operator = parseFloat(operatorAsString);
                    else
                        operator = operand;

                    operand = (float) operationMethod.invoke(null, operand, operator);
                    operator = null;
                    operationMethod = mathOperations.getClass().getMethod(methodName, Float.TYPE, Float.TYPE);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener operationResultListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            try {
                if (operand != null && operator != null) {
                    float result = (float) operationMethod.invoke(null, operand, operator);
                    displayNumber = String.valueOf(result);
                    display.setText(displayNumber);
                    operand = null;
                    operator = null;
                    operationMethod = null;
                }
                else if (displayNumber != "") //If this heapens, only clean the variables, because the display already shows the result
                {
                    operand = null;
                    operator = null;
                    operationMethod = null;
                    displayNumber = "";
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }


        }
    };

    View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            displayNumber = "";
            display.setText(displayNumber);
            operand = null;
            operator = null;
        }
    };

    
}