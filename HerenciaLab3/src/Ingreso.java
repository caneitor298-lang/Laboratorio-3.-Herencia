import java.math.BigDecimal;

public final class Ingreso {
    private final String codigoOrden;
    private final String categoria;
    private final TipoIngreso tipo;
    private final BigDecimal monto;
    public Ingreso(OrdenServicio orden, TipoIngreso tipo, BigDecimal monto) {
        if (orden == null || tipo == null) throw new IllegalArgumentException("Ingreso incompleto.");
        this.codigoOrden = orden.getCodigo();
        this.categoria = orden.categoria();
        this.tipo = tipo;
        this.monto = Validaciones.dinero(Validaciones.montoPositivo(monto, "El ingreso"));
    }
    public String getCodigoOrden() { return codigoOrden; }
    public String getCategoria() { return categoria; }
    public TipoIngreso getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
}
