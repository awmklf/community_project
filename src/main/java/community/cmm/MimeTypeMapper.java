package community.cmm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MimeTypeMapper {
   
	private final Map<String, String> mimeToExtensionMap = new HashMap<String, String>();

    public MimeTypeMapper() {
        mimeToExtensionMap.put("image/jpeg", "jpg");
        mimeToExtensionMap.put("image/png", "png");
        mimeToExtensionMap.put("image/gif", "gif");
        mimeToExtensionMap.put("image/bmp", "bmp");
        mimeToExtensionMap.put("image/webp", "webp");
    }

    public String getExtension(String mimeType) {
        return mimeToExtensionMap.get(mimeType);
    }

}
