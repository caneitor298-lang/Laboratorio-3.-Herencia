import java.math.BigDecimal;

public abstract class OrdenServicio {
    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        this.codigo = Validaciones.codigo(codigo);
        this.cliente = Validaciones.texto(cliente, "El cliente");
        this.direccion = Validaciones.texto(direccion, "La direccion");
        this.horasEstimadas = Validaciones.positivo(horasEstimadas, "Las horas estimadas");
        if (zona == null) throw new IllegalArgumentException("Debe indicar una zona.");
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
    }
    public abstract BigDecimal calcularCosto();
    public abstract int diasAtencion();
    public abstract int maximoVisitas();
    public abstract String categoria();
    public boolean esCompatible(Cuadrilla cuadrilla) { return cuadrilla != null; }
    protected final BigDecimal tarifaBase() {
        return BigDecimal.valueOf(zona == Zona.METROPOLITANA ? 100 : 175);
    }
    public final String getCodigo() { return codigo; }
    public final String getCliente() { return cliente; }
    public final String getDireccion() { return direccion; }
    public final double getHorasEstimadas() { return horasEstimadas; }
    public final Zona getZona() { return zona; }
    public final EstadoOrden getEstado() { return estado; }
    public final int getVisitasRealizadas() { return visitasRealizadas; }
    public final boolean puedeAsignarse() {
        return estado == EstadoOrden.PENDIENTE && visitasRealizadas < maximoVisitas();
    }
    // Visibilidad de paquete: las transiciones las coordina Jornada.
    final void asignar() {
        if (!puedeAsignarse()) throw new IllegalArgumentException("La orden no esta pendiente o agoto sus visitas.");
        estado = EstadoOrden.ASIGNADA;
    }
    final void retirar() {
        if (estado != EstadoOrden.ASIGNADA) throw new IllegalArgumentException("La orden no esta asignada.");
        estado = EstadoOrden.PENDIENTE;
    }
    final void iniciarVisita() {
        if (estado != EstadoOrden.ASIGNADA || visitasRealizadas >= maximoVisitas())
            throw new IllegalArgumentException("La orden no puede iniciar otra visita.");
        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
    }
    final void registrarResultado(boolean completada) {
        if (estado != EstadoOrden.EN_ATENCION)
            throw new IllegalArgumentException("La orden no esta en atencion.");
        estado = completada ? EstadoOrden.COMPLETADA
                : visitasRealizadas >= maximoVisitas() ? EstadoOrden.CANCELADA : EstadoOrden.PENDIENTE;
    }
    @Override public String toString() {
        return codigo + " | " + categoria() + " | " + cliente + " | " + direccion
                + " | " + zona + " | " + horasEstimadas + " h | " + estado
                + " | visitas " + visitasRealizadas + "/" + maximoVisitas()
                + " | Q" + calcularCosto().toPlainString() + " | " + diasAtencion() + " dias habiles";
    }
}
