package us.lsi.sevici;
import java.util.Locale;

public class TestRed {

	
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		Red r = Red.parse("ficheros/estaciones.csv");
		System.out.println(r);
		System.out.println(r.porNumero(250));
		System.out.println(r.porName("CALLE DE SALVADOR ALLENDE"));
//		System.out.println(CollectionsTools.mapToString(r.numeroDeEstacionesPorBicisDisponibles()));
//		r.showUbicaciones();
//		r.escribe(10,"ficheros/estaciones2.txt");
//		List<Coordenadas2D> ubicaciones = r.ubicacionEstacionesDisponibles(25);
//		GraphicsMaps.of(GraphicType.Bing).markers("ficheros/MarkersBingOut.html","green", ubicaciones);
	}

}
