package us.lsi.sevici;

import us.lsi.coordenadas.Coordenadas2D;
import us.lsi.tools.Preconditions;

public record Estacion(Integer numero,
		String name,
		Integer slots,
		Integer empty_slots,
		Integer free_bikes,
		Coordenadas2D coordenadas) {
	
	public static Estacion parse(String linea) {
		String[] partes = linea.split(",");
		String[] sp = partes[0].split("_");
		Integer numero = Integer.parseInt(sp[0]);
		String name = sp[1];
		Integer slots= Integer.parseInt(partes[1]);
		Integer empty_slots = Integer.parseInt(partes[2]);
		Integer free_bikes = Integer.parseInt(partes[3]);
		Preconditions.checkArgument(slots >=0,String.format("Slots %d en %s",slots,linea));
		Preconditions.checkArgument(empty_slots >=0,String.format("Empty_Slots %d en %s",empty_slots,linea));
		Preconditions.checkArgument(free_bikes >=0,String.format("Free_Bikes %d en %s",free_bikes,linea));
		Coordenadas2D coordenadas = Coordenadas2D.of(Double.parseDouble(partes[4]), Double.parseDouble(partes[5]));
		return new Estacion(numero,name,slots,empty_slots,free_bikes,coordenadas);
	}

	@Override
	public String toString() {
		return String.format("(%3d,%30s,%2d,%2d,%2d,%s)",numero,name,slots,empty_slots,free_bikes,coordenadas.toString());
	}
	
}
