

package es.igosoftware.dmvc.commands.client;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.dmvc.commands.GDAbstractCommand;
import es.igosoftware.dmvc.commands.IDCommand;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.ITransformer;


public abstract class GDAbstractClientCommand
         extends
            GDAbstractCommand
         implements
            IDClientCommand {


   private static final long                                     serialVersionUID   = 1L;

   private static final ITransformer<IDCommand, IDClientCommand> CASTER_TRANSFORMER = new ITransformer<IDCommand, IDClientCommand>() {
                                                                                       @Override
                                                                                       public IDClientCommand transform(final IDCommand element) {
                                                                                          return (IDClientCommand) element;
                                                                                       }
                                                                                    };


   @Override
   public IDCommand createComposite(final List<IDCommand> commands,
                                    final GProtocolMultiplexor multiplexor) {
      final GDCompositeClientCommand composite = new GDCompositeClientCommand(GCollections.collect(commands, CASTER_TRANSFORMER),
               multiplexor);
      return composite;
   }


   @Override
   protected final List<IDCommand> getFragmentsCommands(final byte[][] fragments,
                                                        final int fragmentsGroupID) {
      final int fragmentsLength = fragments.length;
      final ArrayList<IDCommand> result = new ArrayList<IDCommand>(fragmentsLength);
      // all but last
      for (int i = 0; i < fragmentsLength - 1; i++) {
         result.add(new GDFragmentClientCommand(fragmentsGroupID, fragments[i], false));
      }
      // last fragment
      result.add(new GDFragmentClientCommand(fragmentsGroupID, fragments[fragmentsLength - 1], true));

      return result;
   }


}