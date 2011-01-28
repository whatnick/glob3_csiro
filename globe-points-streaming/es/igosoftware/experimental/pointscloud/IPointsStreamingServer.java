

package es.igosoftware.experimental.pointscloud;

import java.io.IOException;
import java.util.List;

import es.igosoftware.dmvc.model.IDModel;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;


public interface IPointsStreamingServer
         extends
            IDModel {


   public List<String> getPointsCloudsNames();


   public GPCPointsCloud getPointsCloud(final String pointsCloudName) throws IOException;

}
