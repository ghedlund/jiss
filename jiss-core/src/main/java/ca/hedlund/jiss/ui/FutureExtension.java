package ca.hedlund.jiss.ui;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.jiss.JissModel;

import java.util.concurrent.Future;

@Extension(JissModel.class)
public record FutureExtension(Future<Object> future) {
}
