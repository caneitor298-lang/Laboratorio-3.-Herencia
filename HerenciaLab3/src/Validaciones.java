import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public final class Validaciones {
    private Validaciones() { }
    public static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new IllegalArgumentException(campo + " no puede estar vacio.");
        return valor.trim();
    }
    public static String codigo(String valor) {
        return texto(valor, "El codigo").toUpperCase(Locale.ROOT);
    }
    public static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0)
            throw new IllegalArgumentException(campo + " debe ser positivo y finito.");
        return valor;
    }
    public static int enteroPositivo(int valor, String campo) {
        if (valor <= 0) throw new IllegalArgumentException(campo + " debe ser positivo.");
        return valor;
    }
    public static BigDecimal montoPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0)
            throw new IllegalArgumentException(campo + " debe ser positivo.");
        return valor;
    }
    public static BigDecimal dinero(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
