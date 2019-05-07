package green.atm.extrato;

public interface ProcessadorExtrato {
	public abstract Extrato construirExtrato(String path) throws Exception;
}
