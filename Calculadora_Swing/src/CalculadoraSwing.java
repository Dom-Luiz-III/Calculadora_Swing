import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CalculadoraSwing {
    private JFrame frame;
    private JTextField textField;

    private double num1, num2;
    private String operador;
    private boolean novoCalculo = true;
    private DecimalFormat df;

    public CalculadoraSwing() {
        frame = new JFrame("Calculadora");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());

        textField = new JTextField();
        frame.add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4));

        String[] buttonLabels = {
                "7", "8", "9", "÷",
                "4", "5", "6", "x",
                "1", "2", "3", "-",
                "0", ",", "=", "+",
                "C"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // Configure o formato decimal com vírgula como separador
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        df = new DecimalFormat("#.##########", symbols);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            try {
                if (command.matches("[0-9]") || (command.equals(",") && !textField.getText().contains(","))) {
                    if (novoCalculo) {
                        textField.setText("");
                        novoCalculo = false;
                    }
                    textField.setText(textField.getText() + command);
                } else if (command.matches("[+\\-*/]")) {
                    if (!novoCalculo) {
                        num1 = df.parse(textField.getText()).doubleValue();
                        operador = command;
                        novoCalculo = true;
                    }
                } else if (command.equals("=")) {
                    if (!novoCalculo) {
                        num2 = df.parse(textField.getText()).doubleValue();
                        double resultado = 0;

                        switch (operador) {
                            case "+":
                                resultado = num1 + num2;
                                break;
                            case "-":
                                resultado = num1 - num2;
                                break;
                            case "x":
                                resultado = num1 * num2;
                                break;
                            case "÷":
                                if (num2 != 0) {
                                    resultado = num1 / num2;
                                } else {
                                    textField.setText("Erro: Divisão por zero");
                                    return;
                                }
                                break;
                        }

                        textField.setText(df.format(resultado));
                        novoCalculo = true;
                    }
                } else if (command.equals("C")) { // Limpar
                    textField.setText("");
                    novoCalculo = true;
                }
            } catch (Exception ex) {
                textField.setText("Erro: Formato de número inválido");
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
