import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class CalculadoraSwing {
    private final JTextField textField;
    private final JTextArea historyTextArea;
    private final Stack<Double> operandStack;
    private final Stack<Character> operatorStack;
    private boolean newCalculation;
    private boolean isResultDisplayed;
    private final DecimalFormat df;
    private final StringBuilder currentNumber;
    private final List<JButton> operatorButtons;

    public CalculadoraSwing() {
        JFrame frame = new JFrame("Calculadora Swing");
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

        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(75, 75));

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
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }

    private double performOperation(double a, double b, char operator) {
        switch (operator) {
            case '+' -> {
                return a + b;
            }
            case '-' -> {
                return a - b;
            }
            case '*' -> {
                return a * b;
            }
            case '/' -> {
                if (b != 0) {
                    return a / b;
                } else {
                    textField.setText("Erro: Divisão por zero");
                    isResultDisplayed = true;
                    return 0;
                }
            }
            default -> {
                return 0;
            }
        }
    }

    private void calculateResult() {
        if (!currentNumber.isEmpty()) {
            double currentValue = Double.parseDouble(currentNumber.toString());
            operandStack.push(currentValue);
            currentNumber.setLength(0);
        }

        if (!operatorStack.isEmpty() && operandStack.size() >= 2) {
            while (!operatorStack.isEmpty()) {
                double b = operandStack.pop();
                double a = operandStack.pop();
                char op = operatorStack.pop();
                operandStack.push(performOperation(a, b, op));
            }

            double result = operandStack.pop();
            textField.setText(df.format(result));
            historyTextArea.append(textField.getText() + "\n");
            isResultDisplayed = true;
            operandStack.push(result);
        } else {
            textField.setText("Formato usado inválido!");
            isResultDisplayed = true;
        }
    }

    private void clearAll() {
        textField.setText("");
        currentNumber.setLength(0);
        operandStack.clear();
        operatorStack.clear();
        isResultDisplayed = false;
    }

    // Primeiros testes com Junit
    public static class CalculadoraSwingTest {

        private CalculadoraSwing calculadora;

        @Before
        public void setUp() {
            calculadora = new CalculadoraSwing();
        }

        @Test
        public void testeSoma() {
            assertEquals(4.0, calculadora.performOperation(2.0, 2.0, '+'), 0.0001);
        }

        @Test
        public void testeSubtracao() {
            assertEquals(3.0, calculadora.performOperation(5.0, 2.0, '-'), 0.0001);
        }

        @Test
        public void testeMultiplicacao() {
            assertEquals(10.0, calculadora.performOperation(2.0, 5.0, '*'), 0.0001);
        }

        @Test
        public void testeDivisao() {
            assertEquals(2.5, calculadora.performOperation(5.0, 2.0, '/'), 0.0001);
        }

        @Test
        public void testeDivisaoPorZero() {
            assertEquals(0.0, calculadora.performOperation(5.0, 0.0, '/'), 0.0001);
        }

        @Test
        public void testePrecedenciaMultiplicacaoDivisao() {
            assertEquals(14.0, calculadora.performOperation(2.0, 3.0, '*'), 0.0001);
            assertEquals(4.0, calculadora.performOperation(12.0, 3.0, '/'), 0.0001);
        }

        @Test
        public void testePrecedenciaSomaSubtracao() {
            assertEquals(5.0, calculadora.performOperation(2.0, 3.0, '+'), 0.0001);
            assertEquals(-1.0, calculadora.performOperation(2.0, 3.0, '-'), 0.0001);
        }

        @Test
        public void testeCalculoResultado() {
            calculadora.performOperation(2.0, 3.0, '+');
            calculadora.performOperation(5.0, '*', '=');
            assertEquals(25.0, calculadora.operandStack.peek(), 0.0001);
        }

        @Test
        public void testeCalculoResultadoInvalido() {
            calculadora.performOperation(2.0, 3.0, '+');
            calculadora.performOperation(5.0, '=', '*');
            assertEquals(0, calculadora.operandStack.size());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CalculadoraSwing();
            }
        });
    }
}

