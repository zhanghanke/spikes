package com.novoda.pianohero;

import java.util.Locale;

public class GameModel implements GameMvp.Model {

    private final SongSequenceFactory songSequenceFactory;
    private final SimplePitchNotationFormatter pitchNotationFormatter;

    private Sequence sequence;

    GameModel(
            SongSequenceFactory songSequenceFactory,
            SimplePitchNotationFormatter pitchNotationFormatter) {
        this.songSequenceFactory = songSequenceFactory;
        this.pitchNotationFormatter = pitchNotationFormatter;
    }

    @Override
    public void startGame(StartCallback callback) {
        sequence = songSequenceFactory.maryHadALittleLamb();

        callback.onGameStarted(createViewModel(sequence));
    }

    private RoundViewModel createViewModel(Sequence sequence) {
        int nextNotesPosition = sequence.position();
        String nextNoteAsText = pitchNotationFormatter.format(sequence.get(nextNotesPosition));
        String statusMessage = getStatusMessage(sequence);
        return new RoundViewModel(nextNoteAsText, statusMessage);
    }

    private String getStatusMessage(Sequence sequence) {
        if (sequence.latestError() == null) {
            if (sequence.position() > 0) {
                return String.format(Locale.US, "Woo! Keep going! (%d/%d)", sequence.position() + 1, sequence.length());
            } else {
                return "";
            }
        } else {
            return "Ruhroh, try again!";
        }
    }

    @Override
    public void playGameRound(
            RoundCallback roundCallback,
            CompletionCallback completionCallback,
            Note note
    ) {
        int currentPosition = sequence.position();
        Note expectedNote = sequence.get(currentPosition);
        if (currentPosition == sequence.length() - 1 && note.equals(expectedNote)) {
            completionCallback.onGameComplete();
            return;
        }

        if (note.equals(expectedNote)) {
            this.sequence = new Sequence.Builder(sequence).withLatestError(null).atPosition(currentPosition + 1).build();
            roundCallback.onRoundUpdate(createViewModel(sequence));
        } else {
            Sequence updatedSequence = new Sequence.Builder(sequence).withLatestError(note).build();
            roundCallback.onRoundUpdate(createViewModel(updatedSequence));
        }
    }

}
