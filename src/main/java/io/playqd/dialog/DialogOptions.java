package io.playqd.dialog;

public record DialogOptions(boolean hideOnCloseRequest, boolean preventCloseOnEscKeyPressed) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean hideOnCloseRequest;
        private boolean preventCloseOnEscKeyPressed;

        public Builder hideOnCloseRequest(boolean hideOnCloseRequest) {
            this.hideOnCloseRequest = hideOnCloseRequest;
            return this;
        }

        public Builder preventCloseOnEscKeyPressed(boolean preventCloseOnEscKeyPressed) {
            this.preventCloseOnEscKeyPressed = preventCloseOnEscKeyPressed;
            return this;
        }

        public DialogOptions build() {
            return new DialogOptions(hideOnCloseRequest, preventCloseOnEscKeyPressed);
        }
    }
}
