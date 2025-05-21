'use client'

import React from 'react'

interface Patient {
    id?: string
    firstName: string
    lastName: string
    birthDate: string
    address?: string
    phone?: string
    gender: 'MALE' | 'FEMALE'
    risque?: string
}

interface AddPatientModalProps {
    isOpen: boolean
    onClose: () => void
    onSubmit: (patient: Patient) => void
    formData: Patient
    onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void
}

export default function AddPatientModal({ isOpen, onClose, onSubmit, formData, onChange }: AddPatientModalProps) {
    if (!isOpen) return null

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        onSubmit(formData)
    }

    return (
        <div className="fixed inset-0 z-50 backdrop-blur-sm bg-white/30 flex items-center justify-center">
            <section className="bg-white rounded-xl shadow-[0_8px_20px_rgba(0,0,0,0.05)] max-w-md w-full p-8">
                <form onSubmit={handleSubmit} className="space-y-6">
                    <h2 className="text-center text-3xl font-bold">Ajouter un patient</h2>
                    <input name="firstName" placeholder="Prénom" className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg" value={formData.firstName} onChange={onChange} required />
                    <input name="lastName" placeholder="Nom" className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg" value={formData.lastName} onChange={onChange} required />
                    <input name="birthDate" type="date" className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg" value={formData.birthDate} onChange={onChange} required />
                    <input name="address" placeholder="Adresse" className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg" value={formData.address} onChange={onChange} />
                    <input name="phone" placeholder="Téléphone" className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg" value={formData.phone} onChange={onChange} />
                    <select name="gender" className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg" value={formData.gender} onChange={onChange} required>
                        <option value="" disabled hidden>Genre</option>
                        <option value="MALE">Homme</option>
                        <option value="FEMALE">Femme</option>
                    </select>
                    <div className="flex justify-end space-x-2 pt-2">
                        <button type="button" className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg cursor-pointer hover:bg-gray-300" onClick={onClose}>Annuler</button>
                        <button type="submit" className="px-4 py-2 bg-blue-500 text-white rounded-lg cursor-pointer hover:bg-blue-600">Ajouter</button>
                    </div>
                </form>
            </section>
        </div>
    )
}
