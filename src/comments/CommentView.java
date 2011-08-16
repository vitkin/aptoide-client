/**
 * 
 */
package comments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.TextView;

/**
 * 
 * @author rafael
 *
 */
public class CommentView extends TextView{
	
	private Comment comment;
	
	public CommentView(Context context, Comment comment) {
		super(context);
		this.comment = comment;
		this.setText("Texto"+comment.getText());
	}
	
	/**
	 * 
	 * @return
	 * 
	 */
	public Comment getComment() {
		return comment;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//Rect rect = new Rect();
		//getLocalVisibleRect(rect);
		
        Path path = new Path();
		path.moveTo(0, canvas.getHeight());
        path.lineTo(canvas.getWidth(), 0);
		
		Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        
        canvas.drawPath(path, paint);  
        
	}
	
}
