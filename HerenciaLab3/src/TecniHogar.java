import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TecniHogar {
    private final ArrayList<OrdenServicio> ordenes = new ArrayList<>();
    private final ArrayList<Cuadrilla> cuadrillas = new ArrayList<>();
    private final ArrayList<Jornada> jornadas = new ArrayList<>();
    private final ArrayList<Ingreso> ingresos = new ArrayList<>();
    public TecniHogar(boolean cargarEjemplos) { if (cargarEjemplos) cargarDatosIniciales(); }
    public List<OrdenServicio> getOrdenes() { return Collections.unmodifiableList(ordenes); }
    public List<Cuadrilla> getCuadrillas() { return Collections.unmodifiableList(cuadrillas); }
    public List<Jornada> getJornadas() { return Collections.unmodifiableList(jornadas); }
    public List<Ingreso> getIngresos() { return Collections.unmodifiableList(ingresos); }
    public OrdenServicio buscarOrden(String codigo) {
        String clave = Validaciones.codigo(codigo);
        for (OrdenServicio o : ordenes) if (o.getCodigo().equals(clave)) return o;
        throw new IllegalArgumentException("No existe la orden " + clave + ".");
    }
    public Cuadrilla buscarCuadrilla(String codigo) {
        String clave = Validaciones.codigo(codigo);
        for (Cuadrilla c : cuadrillas) if (c.getCodigo().equals(clave)) return c;
        throw new IllegalArgumentException("No existe la cuadrilla " + clave + ".");
    }
    public Jornada buscarJornada(String codigo) {
        String clave = Validaciones.codigo(codigo);
        for (Jornada j : jornadas) if (j.getCodigo().equals(clave)) return j;
        throw new IllegalArgumentException("No existe la jornada " + clave + ".");
    }
    public String cotizar(OrdenServicio propuesta) {
        if (propuesta == null) throw new IllegalArgumentException("Debe indicar un servicio.");
        return propuesta.categoria() + ": Q" + propuesta.calcularCosto().toPlainString()
                + " | atencion estimada: " + propuesta.diasAtencion() + " dias habiles.";
    }
    public void registrarOrden(OrdenServicio orden) {
        validarOrdenNueva(orden);
        Ingreso ingreso = new Ingreso(orden, TipoIngreso.REGISTRO, orden.calcularCosto());
        ordenes.add(orden);
        ingresos.add(ingreso);
    }
    private void validarOrdenNueva(OrdenServicio orden) {
        if (orden == null || orden.getEstado() != EstadoOrden.PENDIENTE || orden.getVisitasRealizadas() != 0)
            throw new IllegalArgumentException("Debe registrar una orden nueva pendiente sin visitas.");
        for (OrdenServicio o : ordenes) if (o.getCodigo().equals(orden.getCodigo()))
            throw new IllegalArgumentException("Ya existe una orden con ese codigo.");
    }
    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) throw new IllegalArgumentException("Debe indicar una cuadrilla.");
        for (Cuadrilla c : cuadrillas) if (c.getCodigo().equals(cuadrilla.getCodigo()))
            throw new IllegalArgumentException("Ya existe una cuadrilla con ese codigo.");
        cuadrillas.add(cuadrilla);
    }
    public void crearJornada(String codigo, String codigoCuadrilla) {
        String clave = Validaciones.codigo(codigo);
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        for (Jornada j : jornadas) {
            if (j.getCodigo().equals(clave)) throw new IllegalArgumentException("Ya existe una jornada con ese codigo.");
            if (j.getCuadrilla() == cuadrilla && j.getEstado() != EstadoJornada.CERRADA)
                throw new IllegalArgumentException("La cuadrilla ya tiene una jornada sin cerrar.");
        }
        jornadas.add(new Jornada(clave, cuadrilla));
    }
    public void asignar(String jornada, String orden) { buscarJornada(jornada).asignar(buscarOrden(orden)); }
    public void retirar(String jornada, String orden) { buscarJornada(jornada).retirar(buscarOrden(orden)); }
    public void iniciar(String jornada) { ingresos.addAll(buscarJornada(jornada).iniciar()); }
    public void registrarResultado(String jornada, String orden, boolean completada) {
        buscarJornada(jornada).registrarResultado(buscarOrden(orden), completada);
    }
    public BigDecimal ingresoPorTipo(TipoIngreso tipo) {
        BigDecimal total = BigDecimal.ZERO;
        for (Ingreso i : ingresos) if (i.getTipo() == tipo) total = total.add(i.getMonto());
        return Validaciones.dinero(total);
    }
    public BigDecimal ingresoTotal() {
        return ingresoPorTipo(TipoIngreso.REGISTRO).add(ingresoPorTipo(TipoIngreso.VISITA_ADICIONAL));
    }
    public BigDecimal ingresoPorCategoria(String categoria) {
        BigDecimal total = BigDecimal.ZERO;
        for (Ingreso i : ingresos) if (i.getCategoria().equals(categoria)) total = total.add(i.getMonto());
        return Validaciones.dinero(total);
    }
    public String reporteOrdenes() {
        Map<String, int[]> conteos = new LinkedHashMap<>();
        for (OrdenServicio o : ordenes) {
            conteos.putIfAbsent(o.categoria(), new int[EstadoOrden.values().length]);
            conteos.get(o.categoria())[o.getEstado().ordinal()]++;
        }
        StringBuilder r = new StringBuilder("ORDENES POR CATEGORIA Y ESTADO\n");
        for (Map.Entry<String, int[]> fila : conteos.entrySet()) {
            r.append(fila.getKey()).append(": ");
            int total = 0;
            for (EstadoOrden e : EstadoOrden.values()) {
                int n = fila.getValue()[e.ordinal()]; total += n;
                r.append(e).append("=").append(n).append(" ");
            }
            r.append("TOTAL=").append(total).append('\n');
        }
        r.append("TOTAL GENERAL=").append(ordenes.size()).append('\n');
        return r.toString();
    }
    public String reporteIngresos() {
        StringBuilder r = new StringBuilder("INGRESOS\nRegistro: Q")
                .append(ingresoPorTipo(TipoIngreso.REGISTRO).toPlainString())
                .append("\nVisitas adicionales: Q").append(ingresoPorTipo(TipoIngreso.VISITA_ADICIONAL).toPlainString())
                .append("\nTotal: Q").append(ingresoTotal().toPlainString()).append('\n');
        Set<String> categorias = new LinkedHashSet<>();
        for (OrdenServicio o : ordenes) categorias.add(o.categoria());
        for (String c : categorias) r.append(c).append(": Q").append(ingresoPorCategoria(c).toPlainString()).append('\n');
        return r.toString();
    }
    public String reporteJornadas() {
        StringBuilder r = new StringBuilder("JORNADAS SIN CERRAR\n");
        boolean hay = false;
        for (Jornada j : jornadas) if (j.getEstado() != EstadoJornada.CERRADA) {
            hay = true; r.append(j).append(" | capacidad total ")
                    .append(j.getCuadrilla().getCapacidadHoras()).append(" h\n");
        }
        if (!hay) r.append("No hay jornadas sin cerrar.\n");
        return r.toString();
    }
    private void cargarDatosIniciales() {
        // Son datos de demostracion, no ventas: se cargan sin movimientos de ingresos.
        ordenes.add(new Diagnostico("D01", "Ana", "Zona 1", 1, Zona.METROPOLITANA));
        ordenes.add(new Diagnostico("D02", "Luis", "Villa Nueva", 1.5, Zona.PERIFERICA));
        ordenes.add(new Instalacion("I01", "Maria", "Zona 10", 2.5, Zona.METROPOLITANA, 5));
        ordenes.add(new Instalacion("I02", "Carlos", "Mixco", 2.1, Zona.PERIFERICA, 2));
        ordenes.add(new InstalacionCertificada("E01", "Pedro", "Zona 15", 2.5, Zona.METROPOLITANA, 5, new BigDecimal("2000")));
        ordenes.add(new InstalacionCertificada("E02", "Sofia", "San Jose Pinula", 1.2, Zona.PERIFERICA, 2, new BigDecimal("1000")));
        ordenes.add(new MantenimientoAire("A01", "Lucia", "Zona 4", 1.5, Zona.METROPOLITANA, 9000));
        ordenes.add(new MantenimientoAire("A02", "Diego", "Zona 16", 2.25, Zona.METROPOLITANA, 60000));
        registrarCuadrilla(new Cuadrilla("C01", 8, true));
        registrarCuadrilla(new Cuadrilla("C02", 6, false));
        registrarCuadrilla(new Cuadrilla("C03", 4, true));
    }
}
