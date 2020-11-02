package com.example.univirtuscalculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;

import static java.lang.Float.parseFloat;

public class MainActivity2 extends AppCompatActivity {

    TextView display;
    String displayNumber;
    Float operand = null;
    Float operator = null;
    private String operationMethod;
    private MathOperations mathOperations;
    private String currentOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        display = findViewById(R.id.textView);
        displayNumber = new String();
        mathOperations = new MathOperations();
        currentOperation = new String();

        // Region listener binding
        Button clear = (Button) findViewById(R.id.btnClear);
        clear.setOnClickListener(clearListener);

        Button dot = (Button) findViewById(R.id.btnDot);
        dot.setOnClickListener(decimalSimbolListener);

        Button equal = (Button) findViewById(R.id.btnEquals);
        equal.setOnClickListener(operationResultListener);

        //for the number and operation buttons I dinamicly get every button and apply the correct bind
        for (Field field : R.id.class.getFields()) {
            try {
                int fieldId = field.getInt(0);
                if (field.getName().startsWith("btnNumber")) {
                    Button number = null;
                    number = (Button) findViewById(fieldId);
                    number.setOnClickListener(numberListener);
                } else if (field.getName().startsWith("btnOperation")) {
                    Button operation = null;
                    operation = (Button) findViewById(fieldId);
                    operation.setOnClickListener(operationListener);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //end region
    }

    // region listeners logic
    View.OnClickListener numberListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button pressedBtn = (Button) view;
            String number = pressedBtn.getText().toString();
            displayNumber += number;
            display.setText(displayNumber);
        }
    };

    View.OnClickListener decimalSimbolListener = new View.OnClickListener() {
        @Override
        //Only print a decimal separator once per number
        public void onClick(View view) {
            Button pressedBtn = (Button) view;
            String dot = pressedBtn.getText().toString();
            if (displayNumber != "" && displayNumber.indexOf(".") == -1 && !displayNumber.endsWith("."))
                displayNumber += dot;
            if(displayNumber.contains(currentOperation+"."))
            {
                displayNumber = displayNumber.substring(0, displayNumber.length() - 2) + "0.";
            }
            display.setText(displayNumber);
        }
    };

    View.OnClickListener operationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button pressedBtn = (Button) view;
            String operation = pressedBtn.getText().toString();
            String methodName = (String) pressedBtn.getTag();

            //check wheter is a operand atribution or a operator atribution
            if (operand == null && !displayNumber.equals("")) {
                operand = parseFloat(displayNumber);
                displayNumber += " " + operation + " ";
                display.setText(displayNumber);

                operationMethod = methodName;//set what operation may be performed
                currentOperation = operation;//helper for cases when the present button differs of the current operation

            } else if (operand != null && operator == null) {
                int operatorIndex = displayNumber.indexOf(currentOperation);

                //If the user press same operation 2 times, the calculator understand that the user wishes to perform the operation with the same number in the operand and operator
                if (operatorIndex != -1 && !displayNumber.substring(operatorIndex + 1).equals(" ")) {
                    String operatorAsString = displayNumber.substring(operatorIndex + 1);
                    operator = parseFloat(operatorAsString);
                }
                else
                    operator = operand;

                operand = ResolveOperation();

                displayNumber = String.valueOf(operand) + " " + operation + " ";
                display.setText(displayNumber);
                operator = null;
                operationMethod = methodName;
            }

        }
    };

    View.OnClickListener operationResultListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (operand != null) {
                int operatorIndex = displayNumber.indexOf(currentOperation);

                if (operatorIndex != -1 && !displayNumber.substring(operatorIndex + 1).equals(" ")) {
                    String operatorAsString = displayNumber.substring(operatorIndex + 1);
                    operator = parseFloat(operatorAsString);
                }
                else
                    operator = 0F;

                float result = ResolveOperation();
                displayNumber = String.valueOf(result);
                display.setText(displayNumber);
                operand = null;
                operator = null;
                operationMethod = null;
            } else if (!displayNumber.equals("")) //If this heapens, only clean the variables, because the display already shows the result
            {
                operand = null;
                operator = null;
                operationMethod = null;
                displayNumber = "";
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
    // endregion

    //Decides what operation should perform
    public float ResolveOperation() {
        switch (operationMethod) {
            case "Plus":
                return mathOperations.Plus(operand, operator);
            case "Minus":
                return mathOperations.Minus(operand, operator);
            case "Multiplication":
                return mathOperations.Multiplication(operand, operator);
            case "Division":
                return mathOperations.Division(operand, operator);
            default:
                return 0;
        }
    }
}