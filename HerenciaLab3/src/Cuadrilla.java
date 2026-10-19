public final class Cuadrilla {
    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;
    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        this.codigo = Validaciones.codigo(codigo);
        this.capacidadHoras = Validaciones.positivo(capacidadHoras, "La capacidad");
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }
    public String getCodigo() { return codigo; }
    public double getCapacidadHoras() { return capacidadHoras; }
    public boolean isCertificadaRefrigeracion() { return certificadaRefrigeracion; }
    @Override public String toString() {
        return codigo + " | " + capacidadHoras + " h | refrigeracion: " + (certificadaRefrigeracion ? "si" : "no");
    }
}
