import java.math.BigDecimal;

public final class InstalacionCertificada extends Instalacion {
    private final BigDecimal valorMateriales;
    public InstalacionCertificada(String codigo, String cliente, String direccion, double horas,
                                 Zona zona, int puntos, BigDecimal valorMateriales) {
        super(codigo, cliente, direccion, horas, zona, puntos);
        this.valorMateriales = Validaciones.montoPositivo(valorMateriales, "El valor de los materiales");
    }
    public BigDecimal getValorMateriales() { return valorMateriales; }
    @Override public BigDecimal calcularCosto() {
        return Validaciones.dinero(super.calcularCosto().add(BigDecimal.valueOf(120))
                .add(valorMateriales.multiply(new BigDecimal("0.05"))));
    }
    @Override public int diasAtencion() { return super.diasAtencion() + 2; }
    @Override public int maximoVisitas() { return 2; }
    @Override public String categoria() { return "Instalacion electrica certificada"; }
}
