import java.math.BigDecimal;

public final class Diagnostico extends OrdenServicio {
    public Diagnostico(String codigo, String cliente, String direccion, double horas, Zona zona) {
        super(codigo, cliente, direccion, horas, zona);
        if (horas > 1.5) throw new IllegalArgumentException("Un diagnostico no puede superar 1.5 horas.");
    }
    @Override public BigDecimal calcularCosto() {
        return Validaciones.dinero(BigDecimal.valueOf(getZona() == Zona.METROPOLITANA ? 150 : 225));
    }
    @Override public int diasAtencion() { return getZona() == Zona.METROPOLITANA ? 1 : 2; }
    @Override public int maximoVisitas() { return 3; }
    @Override public String categoria() { return "Diagnostico"; }
}
