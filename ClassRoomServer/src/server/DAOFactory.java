package server;

public class DAOFactory {
	private DAOFactory()
	{
		
	}
	
	public static ServerDAO getServerDAO()
	{
		return new ServerDAOImpl();
	}
}
