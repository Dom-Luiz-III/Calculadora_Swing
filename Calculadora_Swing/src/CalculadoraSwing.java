import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CalculadoraSwing {
    private JFrame frame;
    private JTextField textField;
    private JTextArea historyTextArea;
    private Stack<Double> operandStack;
    private Stack<Character> operatorStack;
    private boolean newCalculation;
    private boolean isResultDisplayed;
    private DecimalFormat df;
    private StringBuilder currentNumber;
    private List<JButton> operatorButtons;

    public CalculadoraSwing() {
        frame = new JFrame("Calculadora");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 24));
        frame.add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4));

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C"
        };

        currentNumber = new StringBuilder();
        operandStack = new Stack<>();
        operatorStack = new Stack<>();
        operatorButtons = new ArrayList<>();

        for (String label : buttonLabels) {
            JButton button = createButton(label);
            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);

        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);
        historyTextArea.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Configure o formato decimal com vírgula como separador
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        df = new DecimalFormat("#.##########", symbols);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (Character.isDigit(keyChar) || keyChar == '.') {
                    currentNumber.append(keyChar);
                    textField.setText(textField.getText() + keyChar);
                } else if (keyChar == KeyEvent.VK_ENTER) {
                    calculateResult();
                }
            }
        });
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.PLAIN, 24));

        if (label.matches("[0-9]") || label.equals(".")) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (isResultDisplayed) {
                        clearAll();
                        isResultDisplayed = false;
                    }

                    currentNumber.append(label);
                    textField.setText(textField.getText() + label);
                }
            });
        } else if (label.matches("[+\\-*/]")) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (isResultDisplayed) {
                        currentNumber.setLength(0);
                        isResultDisplayed = false;
                    }

                    char operator = label.charAt(0);
                    if (!currentNumber.toString().isEmpty()) {
                        operandStack.push(Double.parseDouble(currentNumber.toString()));
                        currentNumber.setLength(0);
                    }
                    while (!operatorStack.isEmpty() && getOperatorPrecedence(operator) <= getOperatorPrecedence(operatorStack.peek())) {
                        double b = operandStack.pop();
                        double a = operandStack.pop();
                        char op = operatorStack.pop();
                        operandStack.push(performOperation(a, b, op));
                    }
                    operatorStack.push(operator);
                    textField.setText(textField.getText() + label);
                }
            });
            operatorButtons.add(button);
        } else if (label.equals("=")) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    calculateResult();
                }
            });
        } else if (label.equals("C")) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clearAll();
                }
            });
        }

        return button;
    }

    private int getOperatorPrecedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }

    private double performOperation(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b != 0) {
                    return a / b;
                } else {
                    textField.setText("Erro: Divisão por zero");
                    isResultDisplayed = true;
                    return 0;
                }
            default:
                return 0;
        }
    }

    private void calculateResult() {
        if (currentNumber.length() > 0) {
            double currentValue = Double.parseDouble(currentNumber.toString());
            operandStack.push(currentValue);
            currentNumber.setLength(0);
        }

        while (!operatorStack.isEmpty()) {
            double b = operandStack.pop();
            double a = operandStack.pop();
            char op = operatorStack.pop();
            operandStack.push(performOperation(a, b, op));
        }

        if (!operandStack.isEmpty()) {
            double result = operandStack.pop();
            textField.setText(df.format(result));
            historyTextArea.append(textField.getText() + "\n");
            isResultDisplayed = true;
            operandStack.push(result); // Mantém o resultado para uso em novos cálculos
        }
    }

    private void clearAll() {
        textField.setText("");
        currentNumber.setLength(0);
        operandStack.clear();
        operatorStack.clear();
        isResultDisplayed = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CalculadoraSwing();
            }
        });
    }
}
