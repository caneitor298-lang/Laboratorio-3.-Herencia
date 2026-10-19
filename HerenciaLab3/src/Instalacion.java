import java.math.BigDecimal;

public class Instalacion extends OrdenServicio {
    private final int puntos;
    public Instalacion(String codigo, String cliente, String direccion, double horas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horas, zona);
        this.puntos = Validaciones.enteroPositivo(puntos, "La cantidad de puntos");
    }
    public final int getPuntos() { return puntos; }
    public final double horasPorPuntos() { return puntos * 0.75; }
    public final double horasFacturables() { return Math.ceil(Math.max(getHorasEstimadas(), horasPorPuntos())); }
    @Override public BigDecimal calcularCosto() {
        return Validaciones.dinero(tarifaBase().add(BigDecimal.valueOf(horasFacturables()).multiply(BigDecimal.valueOf(90))));
    }
    @Override public int diasAtencion() { return getZona() == Zona.METROPOLITANA ? 3 : 5; }
    @Override public int maximoVisitas() { return 3; }
    @Override public String categoria() { return "Instalacion"; }
}
