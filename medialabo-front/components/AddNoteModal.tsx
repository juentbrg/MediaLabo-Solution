'use client'

import React, { FC, ChangeEvent, FormEvent } from 'react'

export interface NoteForm {
    patId: string
    note: string
}

interface AddNoteModalProps {
    isOpen: boolean
    onClose: () => void
    onSubmit: (form: NoteForm) => void
    formData: NoteForm
    onChange: (e: ChangeEvent<HTMLTextAreaElement>) => void
}

const AddNoteModal: FC<AddNoteModalProps> = ({
         isOpen,
         onClose,
         onSubmit,
         formData,
         onChange,
     }) => {
    if (!isOpen) return null

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        onSubmit(formData)
    }

    return (
        <div className="fixed inset-0 z-50 bg-black/30 flex items-center justify-center">
            <form
                onSubmit={handleSubmit}
                className="bg-white rounded-lg shadow-xl w-full max-w-md p-6 space-y-4"
            >
                <h2 className="text-2xl font-bold">Ajouter une note</h2>
                <textarea
                    name="note"
                    rows={4}
                    className="w-full border rounded p-2 outline-none border-[#d1d5db]"
                    placeholder="Contenu de la note…"
                    value={formData.note}
                    onChange={onChange}
                    required
                />
                <div className="flex justify-end space-x-2">
                    <button
                        type="button"
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition"
                    >
                        Annuler
                    </button>
                    <button
                        type="submit"
                        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                    >
                        Ajouter
                    </button>
                </div>
            </form>
        </div>
    )
}

export default AddNoteModal
