import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Jornada {
    private final String codigo;
    private final Cuadrilla cuadrilla;
    private final ArrayList<OrdenServicio> ordenes;
    private final ArrayList<OrdenServicio> resueltas;
    private EstadoJornada estado;
    public Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = Validaciones.codigo(codigo);
        if (cuadrilla == null) throw new IllegalArgumentException("Debe indicar una cuadrilla.");
        this.cuadrilla = cuadrilla;
        ordenes = new ArrayList<>();
        resueltas = new ArrayList<>();
        estado = EstadoJornada.PREPARACION;
    }
    public String getCodigo() { return codigo; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public EstadoJornada getEstado() { return estado; }
    public List<OrdenServicio> getOrdenes() { return Collections.unmodifiableList(ordenes); }
    public BigDecimal horasAsignadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrdenServicio o : ordenes) total = total.add(BigDecimal.valueOf(o.getHorasEstimadas()));
        return total;
    }
    public BigDecimal capacidadDisponible() {
        return BigDecimal.valueOf(cuadrilla.getCapacidadHoras()).subtract(horasAsignadas());
    }
    final void asignar(OrdenServicio orden) {
        exigirPreparacion();
        if (orden == null || !orden.puedeAsignarse())
            throw new IllegalArgumentException("La orden no esta pendiente o ya esta asignada.");
        if (!orden.esCompatible(cuadrilla))
            throw new IllegalArgumentException("La cuadrilla no es compatible; el mantenimiento requiere certificacion en refrigeracion.");
        if (BigDecimal.valueOf(orden.getHorasEstimadas()).compareTo(capacidadDisponible()) > 0)
            throw new IllegalArgumentException("La asignacion supera la capacidad de la cuadrilla.");
        orden.asignar();
        ordenes.add(orden);
    }
    final void retirar(OrdenServicio orden) {
        exigirPreparacion();
        if (!ordenes.contains(orden)) throw new IllegalArgumentException("La orden no pertenece a esta jornada.");
        orden.retirar();
        ordenes.remove(orden);
    }
    // Devuelve los cargos ya calculados; TecniHogar los incorpora al libro de ingresos.
    final List<Ingreso> iniciar() {
        exigirPreparacion();
        if (ordenes.isEmpty()) throw new IllegalArgumentException("No puede iniciar una jornada vacia.");
        ArrayList<Ingreso> cargos = new ArrayList<>();
        for (OrdenServicio o : ordenes) {
            if (o.getEstado() != EstadoOrden.ASIGNADA || o.getVisitasRealizadas() >= o.maximoVisitas())
                throw new IllegalArgumentException("Hay una orden que no puede iniciar visita.");
            if (o.getVisitasRealizadas() > 0)
                cargos.add(new Ingreso(o, TipoIngreso.VISITA_ADICIONAL, BigDecimal.valueOf(40)));
        }
        for (OrdenServicio o : ordenes) o.iniciarVisita();
        estado = EstadoJornada.EN_CURSO;
        return cargos;
    }
    final void registrarResultado(OrdenServicio orden, boolean completada) {
        if (estado != EstadoJornada.EN_CURSO) throw new IllegalArgumentException("La jornada no esta en curso.");
        if (!ordenes.contains(orden) || resueltas.contains(orden))
            throw new IllegalArgumentException("La orden no pertenece a la jornada o su resultado ya fue registrado.");
        orden.registrarResultado(completada);
        resueltas.add(orden);
        if (resueltas.size() == ordenes.size()) estado = EstadoJornada.CERRADA;
    }
    private void exigirPreparacion() {
        if (estado != EstadoJornada.PREPARACION) throw new IllegalArgumentException("La jornada no esta en preparacion.");
    }
    @Override public String toString() {
        return codigo + " | " + cuadrilla.getCodigo() + " | " + estado + " | asignadas "
                + horasAsignadas().toPlainString() + " h | disponibles " + capacidadDisponible().toPlainString() + " h";
    }
}
