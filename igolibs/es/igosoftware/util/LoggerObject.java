package es.igosoftware.util;


public abstract class LoggerObject {

   public abstract boolean logVerbose();


   public String logName() {
      return null;
   }


   private String namedMsg(final String msg) {
      final String logName = logName();
      if (logName == null) {
         return msg;
      }
      return logName + ": " + msg;
   }


   public void logInfo(final String msg) {
      if (logVerbose()) {
         Logger.instance().info(namedMsg(msg));
      }
   }


   public void logInfoUnnamed(final String msg) {
      if (logVerbose()) {
         Logger.instance().info(msg);
      }
   }


   public void logSevere(final Throwable e) {
      Logger.instance().severe(e);
   }


   public void logSevere(final String msg) {
      Logger.instance().severe(namedMsg(msg));
   }


   public void logSevere(final String msg,
                         final Throwable e) {
      Logger.instance().severe(msg, e);
   }


   public void logWarning(final String msg) {
      Logger.instance().warning(namedMsg(msg));
   }


   public void logDecreaseIdentationLevel() {
      Logger.instance().decreaseIdentationLevel();
   }


   public void logIncreaseIdentationLevel() {
      Logger.instance().increaseIdentationLevel();
   }


}
