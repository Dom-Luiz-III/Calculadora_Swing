import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculadoraSwing {
    private JFrame frame;
    private JTextField textField;

    private double num1, num2;
    private String operador;

    public CalculadoraSwing() {
        frame = new JFrame("Calculadora");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());

        textField = new JTextField();
        frame.add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4));

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.matches("[0-9]")) {
                textField.setText(textField.getText() + command);
            } else if (command.equals(".")) {
                if (!textField.getText().contains(".")) {
                    textField.setText(textField.getText() + command);
                }
            } else if (command.matches("[+\\-*/]")) {
                num1 = Double.parseDouble(textField.getText());
                operador = command;
                textField.setText("");
            } else if (command.equals("=")) {
                num2 = Double.parseDouble(textField.getText());
                double resultado = 0;

                switch (operador) {
                    case "+":
                        resultado = num1 + num2;
                        break;
                    case "-":
                        resultado = num1 - num2;
                        break;
                    case "*":
                        resultado = num1 * num2;
                        break;
                    case "/":
                        resultado = num1 / num2;
                        break;
                }

                textField.setText(String.valueOf(resultado));
            }
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