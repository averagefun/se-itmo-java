package gui.verifiers;

import javax.swing.*;

public class MyInputVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
//        String text = ((JTextField) input).getText();
//        try {
//            BigDecimal value = new BigDecimal(text);
//            return (value.scale() <= Math.abs(4));
//        } catch (NumberFormatException e) {
//            return false;
//        }
        return true;
    }
}
